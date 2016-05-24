/*
 * Copyright (c) 2016 Samsung Electronics Co., Ltd. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
#include "cairotouchdrawing.h"
#include "view.h"

static struct view_info {
	Evas_Object *win;
	Evas_Object *conform;
	Evas_Object *img;
	Evas_Coord width;
	Evas_Coord height;
	cairo_t *cairo;
	cairo_surface_t *surface;
	unsigned char *pixels;

	/* Variable for start drawing state */
	int touch_drawing_start;

	/* Variables for path start and end point */
	int cur_x;
	int cur_y;
	int prev_x;
	int prev_y;
} s_info = {
	.win = NULL,
	.conform = NULL,
	.touch_drawing_start = 0,
};

/*
 * @brief Create Essential Object window, conformant and layout
 */
void view_create(void)
{
	/* Create window */
	s_info.win = view_create_win(PACKAGE);
	if (s_info.win == NULL) {
		dlog_print(DLOG_ERROR, LOG_TAG, "failed to create a window.");
		return;
	}

	/* Show window after main view is set up */
	evas_object_show(s_info.win);
}

/*
 * @brief Make a basic window named package
 * @param[in] pkg_name Name of the window
 */
Evas_Object *view_create_win(const char *pkg_name)
{
	Evas_Object *win = NULL;

	/*
	 * Window
	 * Create and initialize elm_win.
	 * elm_win is mandatory to manipulate window
	 */
	win = elm_win_util_standard_add(pkg_name, pkg_name);
	elm_win_conformant_set(win, EINA_TRUE);
	elm_win_autodel_set(win, EINA_TRUE);

	/* Rotation setting */
	if (elm_win_wm_rotation_supported_get(win)) {
		int rots[4] = { 0, 90, 180, 270 };
		elm_win_wm_rotation_available_rotations_set(win, (const int *)(&rots), 4);
	}
	/* Add window event callback */
	evas_object_smart_callback_add(win, "delete,request", win_delete_request_cb, NULL);
	eext_object_event_callback_add(win, EEXT_CALLBACK_BACK, win_back_cb, NULL);
	evas_object_event_callback_add(win, EVAS_CALLBACK_RESIZE, win_resize_cb, NULL);
	evas_object_show(win);

	/* Create image */
	s_info.img = evas_object_image_filled_add(evas_object_evas_get(win));
	evas_object_image_content_hint_set(s_info.img, EVAS_IMAGE_CONTENT_HINT_DYNAMIC);
	evas_object_image_colorspace_set(s_info.img, EVAS_COLORSPACE_ARGB8888);
	evas_object_show(s_info.img);

	/* Add mouse event callbacks */
	evas_object_event_callback_add(s_info.img, EVAS_CALLBACK_MOUSE_DOWN, mouse_down_cb, NULL);
	evas_object_event_callback_add(s_info.img, EVAS_CALLBACK_MOUSE_UP, mouse_up_cb, NULL);
	evas_object_event_callback_add(s_info.img, EVAS_CALLBACK_MOUSE_MOVE, mouse_move_cb, NULL);

	return win;
}

/*
 * @brief Destroy window and free important data to finish this application
 */
void view_destroy(void)
{
	if (s_info.win == NULL)
		return;

	/* Destroy cairo surface and device */
	cairo_surface_destroy(s_info.surface);
	cairo_destroy(s_info.cairo);

	evas_object_del(s_info.win);
}

void win_delete_request_cb(void *data, Evas_Object *obj, void *event_info)
{
	view_destroy();
	ui_app_exit();
}

void win_back_cb(void *data, Evas_Object *obj, void *event_info)
{
	/* Let window go to hide state. */
	elm_win_lower(s_info.win);
}

void win_resize_cb(void *data, Evas *e , Evas_Object *obj , void *event_info)
{
	/* When window resize event occurred
		Check first cairo surface already exist
		If cairo surface exist, destroy it */
	if (s_info.surface) {
		/* Destroy previous cairo canvas */
		cairo_surface_destroy(s_info.surface);
		cairo_destroy(s_info.cairo);
		s_info.surface = NULL;
		s_info.cairo = NULL;
	}

	/* When window resize event occurred
		If no cairo surface exist or destroyed
		Create cairo surface with resized Window size */
	if (!s_info.surface) {
		/* Get screen size */
		evas_object_geometry_get(obj, NULL, NULL, &s_info.width, &s_info.height);

		/* Set image size */
		evas_object_image_size_set(s_info.img, s_info.width, s_info.height);
		evas_object_resize(s_info.img, s_info.width, s_info.height);
		evas_object_show(s_info.img);

		/* Create new cairo canvas for resized window */
		s_info.pixels = (unsigned char*)evas_object_image_data_get(s_info.img, 1);
		s_info.surface = cairo_image_surface_create_for_data(s_info.pixels,
						CAIRO_FORMAT_ARGB32, s_info.width, s_info.height, s_info.width * 4);
		s_info.cairo = cairo_create(s_info.surface);

		/* Display default background */
		start_cairo_drawing();
	}
}

/* When user touch down on screen, EVAS_CALLBACK_MOUSE_DOWN event occurred
	At that time this mouse_down_cb function callback called
	In this function, can get and set the touched position and
	If this touch down event is first occurred, can change
	touch_drawing_start's state to enable start drawing */
void mouse_down_cb(void *data, Evas *e, Evas_Object *obj, void *event_info)
{
	Evas_Event_Mouse_Down *ev = (Evas_Event_Mouse_Down *) event_info;

	/* Change the variable's state to enable start drawing */
	if (s_info.touch_drawing_start == 0)
		s_info.touch_drawing_start = 1;

	/* Get previous position from Evas_Event_Mouse_Down event */
	s_info.prev_x = ev->canvas.x;
	s_info.prev_y = ev->canvas.y;
}

/* When user touch off on screen, EVAS_CALLBACK_MOUSE_UP event occurred
	At that time this mouse_up_cb function callback called
	In this function, get and set the touch end position
	Can draw a line from down event position to up event position */
void mouse_up_cb(void *data, Evas *e, Evas_Object *obj, void *event_info)
{
	Evas_Event_Mouse_Up *ev = (Evas_Event_Mouse_Up *) event_info;

	/* Get current position from Evas_Event_Mouse_Up event */
	s_info.cur_x = ev->canvas.x;
	s_info.cur_y = ev->canvas.y;

	/* Check touch down and up event occurred positions are changed */
	if (s_info.prev_x != s_info.cur_x && s_info.prev_y != s_info.cur_y)	{

		/* Set line as white color and width as 8 */
		cairo_set_line_width(s_info.cairo, 8);
		cairo_set_source_rgba(s_info.cairo, 1.0, 1.0, 1.0, 1.0);

		/* Draw a line from point (prev_x, prev_y) to point (cur_x, cur_y) */
		cairo_move_to(s_info.cairo, s_info.prev_x, s_info.prev_y);
		cairo_line_to(s_info.cairo, s_info.cur_x, s_info.cur_y);

		/* Stroke a line with configured color and width */
		cairo_stroke(s_info.cairo);

		/* Render stacked cairo APIs on cairo context's surface */
		cairo_surface_flush(s_info.surface);

		/* Display this cairo line drawing on screen */
		evas_object_image_data_update_add(s_info.img, 0, 0, s_info.width, s_info.height);
	}
}

/* When user touch and move on screen, EVAS_CALLBACK_MOUSE_MOVE event occurred
	At that time this mouse_move_cb function callback called
	In this function, can get the mouse moved from some position to other position
	And set the moved position with inputs */
void mouse_move_cb(void *data, Evas *e, Evas_Object *obj, void *event_info)
{
	Evas_Event_Mouse_Move *ev = (Evas_Event_Mouse_Move *)event_info;
	if (s_info.touch_drawing_start == 1) {
		/* Get touch moved position values */
		s_info.cur_x = ev->cur.canvas.x;
		s_info.cur_y = ev->cur.canvas.y;
		s_info.prev_x = ev->prev.canvas.x;
		s_info.prev_y = ev->prev.canvas.y;

		/* Set a line width as 8, color as white */
		cairo_set_line_width(s_info.cairo, 8);
		cairo_set_source_rgba(s_info.cairo, 1.0, 1.0, 1.0, 1.0);

		/* Draw a line from point (cur_x, cur_y) to point (prev_x, prev_y) */
		cairo_move_to(s_info.cairo, s_info.prev_x, s_info.prev_y);
		cairo_line_to(s_info.cairo, s_info.cur_x, s_info.cur_y);

		/* Stroke a line with configured color and width */
		cairo_stroke(s_info.cairo);

		/* Render stacked cairo APIs on cairo context's surface */
		cairo_surface_flush(s_info.surface);

		/* Display this cairo line drawing on screen */
		evas_object_image_data_update_add(s_info.img, 0, 0, s_info.width, s_info.height);
	}
}

/* In this function, first paint with white color as background
	After paint background, paint again blue color with opacity 0.3 value
	This blue background will be covered with white line
	When user touch event occurred */
void start_cairo_drawing(void)
{
	/* Paint background as white color */
	cairo_set_source_rgba(s_info.cairo, 1.0, 1.0, 1.0, 1.0);
	cairo_paint(s_info.cairo);

	/* Paint Blue color */
	cairo_set_source_rgba(s_info.cairo, 0.2, 0.2, 1.0, 0.3);
	cairo_paint(s_info.cairo);

	/* Render stacked cairo APIs on cairo context's surface */
	cairo_surface_flush(s_info.surface);

	/* Display this cairo painting on screen */
	evas_object_image_data_update_add(s_info.img, 0, 0, s_info.width, s_info.height);
}
