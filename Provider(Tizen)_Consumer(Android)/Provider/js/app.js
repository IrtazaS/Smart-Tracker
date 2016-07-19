/*
* Copyright (c) 2015 Samsung Electronics Co., Ltd.
* All rights reserved.
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions are
* met:
*
* * Redistributions of source code must retain the above copyright
* notice, this list of conditions and the following disclaimer.
* * Redistributions in binary form must reproduce the above
* copyright notice, this list of conditions and the following disclaimer
* in the documentation and/or other materials provided with the
* distribution.
* * Neither the name of Samsung Electronics Co., Ltd. nor the names of its
* contributors may be used to endorse or promote products derived from
* this software without specific prior written permission.
*
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
* "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
* LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
* A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
* OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
* SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
* LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
* DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
* THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
* (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
* OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

var SAAgent,
    SASocket,
    connectionListener,
    responseTxt = document.getElementById("responseTxt");
var xlist = [];
var ylist = [];
var zlist = [];
var count = 0;

/* Make Provider application running in background */
tizen.application.getCurrentApplication().hide();

function createHTML(log_string)
{
    var content = document.getElementById("toast-content");
    content.innerHTML = log_string;
    tau.openPopup("#toast");
}

connectionListener = {
    /* Remote peer agent (Consumer) requests a service (Provider) connection */
    onrequest: function (peerAgent) {

        createHTML("peerAgent: peerAgent.appName<br />" +
                    "is requsting Service conncetion...");

        /* Check connecting peer by appName*/
        if (peerAgent.appName === "HelloAccessoryConsumer") {
            SAAgent.acceptServiceConnectionRequest(peerAgent);
            createHTML("Service connection request accepted.");

        } else {
            SAAgent.rejectServiceConnectionRequest(peerAgent);
            createHTML("Service connection request rejected.");

        }
    },

    /* Connection between Provider and Consumer is established */
    onconnect: function (socket) {
        var onConnectionLost,
            dataOnReceive;

        createHTML("Service connection established");

        /* Obtaining socket */
        SASocket = socket;

        onConnectionLost = function onConnectionLost (reason) {
            createHTML("Service Connection disconnected due to following reason:<br />" + reason);
        };

        /* Inform when connection would get lost */
        SASocket.setSocketStatusListener(onConnectionLost);

        dataOnReceive =  function dataOnReceive (channelId, data) {
            var newData;

            if (!SAAgent.channelIds[0]) {
                createHTML("Something goes wrong...NO CHANNEL ID!");
                return;
            }
            newData = data + " :: " + new Date();

            /* Send new data to Consumer */
            SASocket.sendData(SAAgent.channelIds[0], newData);
            createHTML("Send massage:<br />" +
                        newData);
        };

        /* Set listener for incoming data from Consumer */
        SASocket.setDataReceiveListener(dataOnReceive);
    },
    onerror: function (errorCode) {
        createHTML("Service connection error<br />errorCode: " + errorCode);
    }
};

function requestOnSuccess (agents) {
    var i = 0;

    for (i; i < agents.length; i += 1) {
        if (agents[i].role === "PROVIDER") {
            createHTML("Service Provider found!<br />" +
                        "Name: " +  agents[i].name);
            SAAgent = agents[i];
            break;
        }
    }

    /* Set listener for upcoming connection from Consumer */
    SAAgent.setServiceConnectionListener(connectionListener);
};

function requestOnError (e) {
    createHTML("requestSAAgent Error" +
                "Error name : " + e.name + "<br />" +
                "Error message : " + e.message);
};

/* Requests the SAAgent specified in the Accessory Service Profile */
webapis.sa.requestSAAgent(requestOnSuccess, requestOnError);


(function () {
    /* Basic Gear gesture & buttons handler */
    window.addEventListener('tizenhwkey', function(ev) {
        var page,
            pageid;

        if (ev.keyName === "back") {
            page = document.getElementsByClassName('ui-page-active')[0];
            pageid = page ? page.id : "";
            if (pageid === "main") {
                try {
                    tizen.application.getCurrentApplication().exit();
                } catch (ignore) {
                }
            } else {
                window.history.back();
            }
        }
    });
}());

var xold, yold, zold;
var accelcounter = 0;
var date = new Date();
var time;
window.addEventListener('devicemotion', function(e) {
//	var ax = e.accelerationIncludingGravity.x;
//	var ay = -e.accelerationIncludingGravity.y;
//	var az = -e.accelerationIncludingGravity.z;
	time = date.getTime();
	var ax = e.acceleration.x;
	var ay = -e.acceleration.y;
	var az = -e.acceleration.z;
	document.getElementById("xaccel").innerHTML =  'X : ' +  ax.toFixed(4);
	document.getElementById("yaccel").innerHTML = 'Y : ' + ay.toFixed(4);
	document.getElementById("zaccel").innerHTML = 'Z : ' + az.toFixed(4);
	var rotx = e.rotationRate.alpha ;
	document.getElementById("rotx").innerHTML = 'rotx : ' + rotx.toFixed(4);
	var roty = e.rotationRate.beta ;
	document.getElementById("roty").innerHTML = 'roty : ' + roty.toFixed(4);
	
//	var pData = {
//            calorie: e.cumulativeCalorie,
//            distance: e.cumulativeDistance,
//            runDownStep: e.cumulativeRunDownStepCount,
//            runStep: e.cumulativeRunStepCount,
//            runUpStep: e.cumulativeRunUpStepCount,
//            speed: e.speed,
//            stepStatus: e.stepStatus,
//            totalStep: e.cumulativeTotalStepCount,
//            walkDownStep: e.cumulativeWalkDownStepCount,
//            walkStep: e.cumulativeWalkStepCount,
//            walkUpStep: e.cumulativeWalkUpStepCount,
//            walkingFrequency: e.walkingFrequency
//        };
	//document.getElementById("rotx").innerHTML = 'speed : ' + String(pData.speed);
	var xPos = ax;//.toFixed(0);
	var yPos = ay;//.toFixed(0);
	var zPos = az;
	/* Send new data to Consumer */
	//xlist.push(ax);
	//ylist.push(ay);
	//zlist.push(az);
	count++;
	if(count == 3)
		{
		var xpos = median(xlist);
		var ypos = median(ylist);
		var zpos = median(ylist);
		count = 0;
    SASocket.sendData(SAAgent.channelIds[0], "x"+ax);
    SASocket.sendData(SAAgent.channelIds[0], "y"+ay);
    SASocket.sendData(SAAgent.channelIds[0], "z"+az);
    SASocket.sendData(SAAgent.channelIds[0], "t"+time);
		}
//    createHTML("rotx:<br />" +
//                rotx);

});

function median(values) {

    values.sort( function(a,b) {return a - b;} );

    var half = Math.floor(values.length/2);

    if(values.length % 2)
        return values[half];
    else
        return (values[half-1] + values[half]) / 2.0;
}

(function(tau) {
    var toastPopup = document.getElementById('toast');

    toastPopup.addEventListener('popupshow', function(ev){
        setTimeout(function () {
            tau.closePopup();
        }, 3000);
    }, false);
})(window.tau);