/**
 *  Child Distance Sensor
 *
 *  Copyright 2017 Daniel Ogorchock
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 *  Change History:
 *
 *    Date        Who            What
 *    ----        ---            ----
 *    2020-03-30  Fred Cassirer  Modified from Ultrasonic sensor for simple distance
 *    2018-06-02  Dan Ogorchock  Revised/Simplified for Hubitat Composite Driver Model
 *
 *
 *
 */
metadata {
	definition (name: "Child Distance Sensor", namespace: "ogiewon", author: "Fred Cassirer") {
		capability "Sensor"

		attribute "lastUpdated", "String"
        attribute "distance", "Number"
    }

	tiles(scale: 2) {
		multiAttributeTile(name: "distance", type: "generic", width: 6, height: 4, canChangeIcon: true) {
			tileAttribute("device.distance", key: "PRIMARY_CONTROL") {
				attributeState("distance", label: '${currentValue} in', unit:"in", defaultState: true,
						backgroundColors: [
							[value: 80, color: "#767676"],
							[value: 50, color: "#ffa81e"],
							[value: 20, color: "#d04e00"]
						])
			}
            tileAttribute ("device.feet", key: "SECONDARY_CONTROL") {
        		attributeState "power", label:'Water is: ${currentValue} feet', icon: "http://cdn.device-icons.smartthings.com/Bath/bath6-icn@2x.png"
            }
        }
 		valueTile("lastUpdated", "device.lastUpdated", inactiveLabel: false, decoration: "flat", width: 6, height: 2) {
    			state "default", label:'Last Updated ${currentValue}', backgroundColor:"#ffffff"
		}
    }

    preferences {
        input name: "height", type: "number", title: "Height", description: "Enter height of the sensor (inches)", required: false
        input name: "offset", type: "number", title: "Offset", description: "Enter offset (inches)", required: false
    }
}

def parse(String description) {
    log.debug "parse(${description}) called"
	def parts = description.split(" ")
    def name  = parts.length>0?parts[0].trim():null
    def value = parts.length>1?parts[1].trim():null
    if (name && value) {
        
        float sensorValue = value as float
        log.debug("Value is " + sensorValue)
        log.debug("height is $height")
        float inc = sensorValue * 0.393701
        float inches = 0.0
        if (height) {
           inches = h - inc
        } else {
           inches = inc
        }
        log.debug("inc is " + inc)
       
        float feet = inches / 12.0
        sendEvent(name: "feet", value: String.format("%.2f",feet))
        sendEvent(name: name, value: String.format("%.2f", inches))

        // Update lastUpdated date and time
        def nowDay = new Date().format("MMM dd", location.timeZone)
        def nowTime = new Date().format("h:mm a", location.timeZone)
        sendEvent(name: "lastUpdated", value: nowDay + " at " + nowTime, displayed: false)
    }
    else {
    	log.debug "Missing either name or value.  Cannot parse!"
    }
}

def installed() {

}
