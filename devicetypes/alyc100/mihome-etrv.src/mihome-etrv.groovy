/**
 *  MiHome eTRV
 *
 *  Copyright 2016 Alex Lee Yuk Cheung
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
 *
 *	VERSION HISTORY
 *  	28.02.2018	2.2 - Added battery indication tile to to warn of low battery
 *	21.02.2018	2.1	- re run upto 5 times when set temp is not 200
 *	23.11.2016:	2.0 - Remove BETA status.
 * 
 *	07.11.2016: 2.0 BETA Release 1.1 - Allow icon to be changed.
 *	07.11.2016: 2.0 BETA Release 1 - Version number update to match Smartapp.
 *
 *	10.01.2016: 1.1.2 - Bug fix to Boost mode not executing.
 *
 *	10.01.2016: 1.1.1 - Fixed stopBoost always returning to 'on' mode.
 *
 *	09.01.2016: 1.1 - Added BETA Boost Capability
 *
 *  09.01.2016: 1.0 - Initial Release
 *
 */
 
metadata {
	definition (name: "MiHome eTRV", namespace: "alyc100", author: "Alex Lee Yuk Cheung") {
		capability "Actuator"
		capability "Polling"
		capability "Refresh"
		capability "Temperature Measurement"
        capability "Thermostat"
        capability "Thermostat Mode"
		capability "Thermostat Heating Setpoint"
		capability "Switch"
        capability "Battery"
        
        command "heatingSetpointUp"
		command "heatingSetpointDown"
        command "setHeatingSetpoint"
        command "setBoostLength"
	}

	simulator {
		// TODO: define status and reply messages here
	}

	tiles(scale: 2) {

		multiAttributeTile(name: "thermostat", type:"lighting", width:6, height:4) {
			tileAttribute("device.temperature", key:"PRIMARY_CONTROL", canChangeBackground: true){
				attributeState "default", label: '${currentValue}°', unit:"C", 
                backgroundColors:[
					[value: 0, color: "#153591"],
					[value: 10, color: "#1e9cbb"],
					[value: 13, color: "#90d2a7"],
					[value: 17, color: "#44b621"],
					[value: 20, color: "#f1d801"],
					[value: 25, color: "#d04e00"],
					[value: 29, color: "#bc2323"]
				]
			}
            
            tileAttribute ("lastupdatetemp", key: "SECONDARY_CONTROL") {
				attributeState "default", label:'eTRV last seen ${currentValue}'
            }
		}
        
        valueTile("thermostat_small", "device.temperature", width: 2, height: 2, canChangeIcon: true) {
			state "default", label:'${currentValue}°', unit:"C",
            backgroundColors:[
                [value: 0, color: "#153591"],
					[value: 10, color: "#1e9cbb"],
					[value: 13, color: "#90d2a7"],
					[value: 17, color: "#44b621"],
					[value: 20, color: "#f1d801"],
					[value: 25, color: "#d04e00"],
					[value: 29, color: "#bc2323"]
            ]
		}
 
        valueTile("heatingSetpoint", "device.heatingSetpoint", width: 2, height: 2) {
			state "default", label:'${currentValue}°', unit:"C",
            backgroundColors:[
					[value: 0, color: "#153591"],
					[value: 10, color: "#1e9cbb"],
					[value: 13, color: "#90d2a7"],
					[value: 17, color: "#44b621"],
					[value: 20, color: "#f1d801"],
					[value: 25, color: "#d04e00"],
					[value: 29, color: "#bc2323"]
				]
		}
        
        standardTile("refresh", "device.refresh", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state("default", label:'refresh', action:"polling.poll", icon:"st.secondary.refresh-icon")
		}
 
        controlTile("heatSliderControl", "device.heatingSetpoint", "slider", height: 2, width: 4, inactiveLabel: false, range:"(12..30)") {
			state "setHeatingSetpoint", label:'Set temperature to', action:"setHeatingSetpoint"
		}
        
        controlTile("boostSliderControl", "device.boostLength", "slider", height: 2, width: 4, inactiveLabel: false, range:"(60..300)") {
			state ("setBoostLength", label:'Set boost length to', action:"setBoostLength")
		}
        
        standardTile("switch", "device.switch", decoration: "flat", height: 2, width: 2, inactiveLabel: false) {
			state "on", label:'${name}', action:"switch.off", icon:"st.Home.home1", backgroundColor:"#f1d801"
			state "off", label:'${name}', action:"switch.on", icon:"st.Home.home1", backgroundColor:"#ffffff"
		}
        
        standardTile("thermostatMode", "device.thermostatMode", inactiveLabel: true, decoration: "flat", width: 2, height: 2) {
			state("auto", label: "SCHEDULED", icon:"st.Office.office7")
			state("off", icon:"st.thermostat.heating-cooling-off")
			state("heat", label: "MANUAL", icon:"st.Weather.weather2")
			state("emergency heat", label: "BOOST", icon:"st.Health & Wellness.health7")
		}
        
         valueTile("boost", "device.boostLabel", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state("default", label:'${currentValue}', action:"emergencyHeat")
		}
        valueTile("battery", "device.batteryVoltage", width: 2, height: 2) {
			state "default", label:'Battery Voltage Is ${currentValue}', unit:"V",
            backgroundColors:[
               					[value: 3, color: "#44b621"],
								[value: 2.8, color: "#f1d801"],
								[value: 2.78, color: "#bc2323"],
         		           ]
        }
        main(["thermostat_small"])
		details(["thermostat", "heatingSetpoint", "heatSliderControl", "boost", "boostSliderControl", "switch", "refresh", "battery"])
	}
}

def installed() {
	log.debug "Executing 'installed'"
    state.boostLength = 60
}

def uninstalled() {
    unschedule()
}

// parse events into attributes
def parse(String description) {
	log.debug "Parsing '${description}'"
	// TODO: handle 'temperature' attribute
	// TODO: handle 'heatingSetpoint' attribute
	// TODO: handle 'thermostatSetpoint' attribute
	// TODO: handle 'thermostatMode' attribute
	// TODO: handle 'thermostatOperatingState' attribute
}

// handle commands
def setHeatingSetpoint(temp) {
	state.counter = state.counter
	log.debug "Executing 'setHeatingSetpoint with temp $temp'"
	def latestThermostatMode = device.latestState('thermostatMode')
    
    if (temp < 12) {
		temp = 12
	}
	if (temp > 30) {
		temp = 30
	}
    sendEvent(name: "boostSwitch", value: "off", displayed: false)
    def resp = parent.apiGET("/subdevices/set_target_temperature?params=" + URLEncoder.encode(new groovy.json.JsonBuilder([id: device.deviceNetworkId.toInteger(), temperature: temp]).toString()))
	log.debug ("[${resp.status}] ${resp.data}")
    if (resp.status != 200) {
		log.error("Unexpected result in seting temp: [${resp.status}] ${resp.data}")
// mc re-run upto 5 times
        if (state.counter == null || state.counter >= 5) {
			state.counter = 0
		}
        	if (state.counter == 5) {
            	log.error ("error ran 5 times unsucsesfull")
                state.counter = 0
                return []
               }
		state.counter = state.counter + 1
        log.error ("running set temp again No. ${state.counter.value} attempt")
        runIn (02, setHeatingSetpoint(temp))
		}

   	else {
    	state.counter = 0
        log.debug ("counter value ${state.counter.value}")
        runIn(1, refresh)
    } 
}

def setBoostLength(minutes) {
	log.debug "Executing 'setBoostLength with length $minutes minutes'"
    if (minutes < 60) {
		minutes = 60
	}
	if (minutes > 300) {
		minutes = 300
	}
    state.boostLength = minutes
    sendEvent(name:"boostLength", value: state.boostLength, displayed: true)
    
    def latestThermostatMode = device.latestState('thermostatMode')
    
    //If already in BOOST mode, send updated boost length.
	if (latestThermostatMode.stringValue == 'emergency heat') {
		setThermostatMode('emergency heat')
    }
    else {
    	refresh()
    }    
}

def stopBoost() {
	log.debug "Executing 'stopBoost'"
	sendEvent(name: "boostSwitch", value: "off", displayed: false)
    if (state.lastThermostatMode == 'off') {
    	off()
    }
    else {
    	on()
    }
}

def heatingSetpointUp(){
	log.debug "Executing 'heatingSetpointUp'"
	int newSetpoint = device.currentValue("heatingSetpoint") + 1
	log.debug "Setting heat set point up to: ${newSetpoint}"
	setHeatingSetpoint(newSetpoint)
}

def heatingSetpointDown(){
	log.debug "Executing 'heatingSetpointDown'"
	int newSetpoint = device.currentValue("heatingSetpoint") - 1
	log.debug "Setting heat set point down to: ${newSetpoint}"
	setHeatingSetpoint(newSetpoint)
}

def setLastHeatingSetpoint(temp) {
	//Don't store set point if it is 12.
	if (temp > 12) {
		state.lastHeatingSetPoint = temp
    }
}

def off() {
	setThermostatMode('off')
    
}

def on() {
	setThermostatMode('heat')
	
}

def heat() {
	setThermostatMode('heat')
}

def emergencyHeat() {
	log.debug "Executing 'boost'"
	
    def latestThermostatMode = device.latestState('thermostatMode')
    
    //Don't do if already in BOOST mode.
	if (latestThermostatMode.stringValue != 'emergency heat') {
		setThermostatMode('emergency heat')
    }
    else {
    	log.debug "Already in boost mode."
    }
}

def auto() {
	setThermostatMode('heat')
}

def setThermostatMode(mode) {
	mode = mode == 'cool' ? 'heat' : mode
	log.debug "Executing 'setThermostatMode with mode $mode'"
    
    if (mode == 'off') {
    	unschedule(stopBoost)
        sendEvent(name: "boostSwitch", value: "off", displayed: false)
    	setLastHeatingSetpoint(device.currentValue('heatingSetpoint'))
    	setHeatingSetpoint(12)
    } else if (mode == 'emergency heat') { 
    	if (state.boostLength == null || state.boostLength == '')
        {
        	state.boostLength = 60
            sendEvent("name":"boostLength", "value": 60, displayed: true)
        }
        state.lastThermostatMode = device.latestState('thermostatMode')
        setLastHeatingSetpoint(device.currentValue('heatingSetpoint'))
        sendEvent(name: "boostSwitch", value: "on", displayed: false)
        def resp = parent.apiGET("/subdevices/set_target_temperature?params=" + URLEncoder.encode(new groovy.json.JsonBuilder([id: device.deviceNetworkId.toInteger(), temperature: 22]).toString()))
        if (resp.status != 200) {
			log.error("Unexpected result in therm mode poll(): [${resp.status}] ${resp.data}")
		runIn (02, setThermostatMode(mode))
        }
   	 	else {
    		refresh()
    	}  
        //Schedule boost switch off
        schedule(now() + (state.boostLength * 60000), stopBoost)
    } else {
    	unschedule(stopBoost)
        sendEvent(name: "boostSwitch", value: "off", displayed: false)
    	def lastHeatingSetPoint = 21
        if (state.lastHeatingSetPoint != null && state.lastHeatingSetPoint > 12)
        {
        	lastHeatingSetPoint = state.lastHeatingSetPoint
        }
    	setHeatingSetpoint(lastHeatingSetPoint)
    }

}

def poll() {
    log.debug "Executing 'poll' for ${device} ${this} ${device.deviceNetworkId}"
    
    def resp = parent.apiGET("/subdevices/show?params=" + URLEncoder.encode(new groovy.json.JsonBuilder([id: device.deviceNetworkId.toInteger()]).toString()))
	log.debug ("response : [${resp.status}] ${resp.data}")
    if (resp.status != 200) {
		log.error("Unexpected result in poll(): [${resp.status}] ${resp.data}")
        log.debug "re executing poll"
        runIn(02, poll)
		return []
	}
    
    //Boost button label
    if (state.boostLength == null || state.boostLength == '')
    {
        state.boostLength = 60
        sendEvent("name":"boostLength", "value": 60, displayed: true)
    }
	def boostLabel = ""
    
	sendEvent(name: "temperature", value: resp.data.data.last_temperature, unit: "C", state: "heat")
    sendEvent(name: "heatingSetpoint", value: resp.data.data.target_temperature, unit: "C", state: "heat")
    def boostSwitch = device.currentValue("boostSwitch")
    log.debug "boostSwitch: $boostSwitch"
    if (boostSwitch != null && boostSwitch == "on") {
    	sendEvent(name: "thermostatMode", value: "emergency heat")
        boostLabel = "Boosting"
    }
    else {
		sendEvent(name: "thermostatMode", value: resp.data.data.target_temperature == 12 ? "off" : "heat")
        boostLabel = "Start\n$state.boostLength Min Boost"
    }
    sendEvent(name: 'thermostatOperatingState', value: resp.data.data.target_temperature == 12 ? "idle" : "heating")
    sendEvent(name: 'thermostatFanMode', value: "off", displayed: false)
    sendEvent(name: "switch", value: resp.data.data.target_temperature == 12 ? "off" : "on")
    sendEvent(name: "batteryVoltage", value: resp.data.data.voltage == null ? "Not Available" : resp.data.data.voltage)
    sendEvent(name: "boostLabel", value: boostLabel, displayed: false)
    sendEvent(name: "lastupdatetemp", value: resp.data.data.updated_at == null ? "Not Available" : resp.data.data.updated_at)
    
    return []
	
}

def refresh() {
	log.debug "Executing 'refresh'"
	runIn(02, poll)
}
