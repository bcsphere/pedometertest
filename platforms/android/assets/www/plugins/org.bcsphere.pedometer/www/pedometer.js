cordova.define("org.bcsphere.pedometer.pedometer", function(require, exports, module) { /*
	Copyright 2013-2014, JUMA Technology

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

		http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
*/


		var exec = require('cordova/exec');
		var platform = require('cordova/platform');
	
		/**
		 * 
		 */
		var pedometer = {
	
			getSteps : function(successFunc,errorFunc){
				exec(successFunc,errorFunc,'Pedometer','getSteps',[]);
			},
	
			clearSteps : function(successFunc,errorFunc){
				exec(successFunc,errorFunc,'Pedometer','clearSteps',[]);
			},
	
			getDegrees : function(successFunc,errorFunc){
				exec(successFunc,errorFunc,'Pedometer','getDegrees',[]);
			},

			getStepLength : function(successFunc,errorFunc){
				exec(successFunc,errorFunc,'Pedometer','getStepLength',[]);
			},
	
		};
		module.exports = pedometer;

});
