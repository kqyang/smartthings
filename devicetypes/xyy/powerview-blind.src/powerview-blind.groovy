/**
 *  Copyright 2017 KongQun Yang
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not
 *  use this file except in compliance with the License. You may obtain a copy
 *  of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *  WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *  License for the specific language governing permissions and limitations
 *  under the License.
 *
 */

metadata {
  definition (name: "Powerview Blind", namespace: "xyy",
              author: "KongQun Yang") {
    capability "Switch Level"
    capability "Switch"
    capability "Window Shade"
    capability "Refresh"
    capability "Actuator"
    // IFTTT only supports on/off, lock/unlock...
    // So use lock to send tilt command.
    capability "Lock"

    command "tilt"
    attribute "tiltLevel", "number"
  }

  preferences {
  }

  simulator {
  }

  tiles(scale: 2) {
    multiAttributeTile(name:"shade", type: "generic", width: 6, height: 4) {
      tileAttribute("device.windowShade", key: "PRIMARY_CONTROL") {
        attributeState("unknown", label:'${name}', action:"refresh.refresh",
                       icon:"st.doors.garage.garage-open",
                       backgroundColor:"#ffa81e")
        attributeState("closed", label:'${name}', action:"presetPosition",
                       icon:"st.doors.garage.garage-closed",
                       backgroundColor:"#bbbbdd", nextState:"partially open")
        attributeState("open", label:'${name}', action:"close",
                       icon:"st.doors.garage.garage-open",
                       backgroundColor:"#ffcc33", nextState:"closed")
        attributeState("partially open", label:'preset', action:"close",
                       icon:"st.Transportation.transportation13",
                       backgroundColor:"#ffcc33", nextState:"closed")
        attributeState("tilt", label:'${name}', action:"close",
                       icon:"st.doors.garage.garage-closed",
                       backgroundColor:"#ddc288", nextState:"closed")
        attributeState("closing", label:'${name}', action:"presetPosition",
                       icon:"st.doors.garage.garage-closing",
                       backgroundColor:"#bbbbdd")
        attributeState("opening", label:'${name}', action:"presetPosition",
                       icon:"st.doors.garage.garage-opening",
                       backgroundColor:"#ffcc33")
      }
      tileAttribute ("device.level", key: "SLIDER_CONTROL") {
        attributeState("level", action:"switch level.setLevel")
      }
      tileAttribute ("tiltLevel", key: "VALUE_CONTROL") {
        attributeState("level", action: "tilt")
      }
    }

    standardTile("switchmain", "device.windowShade") {
      state("unknown", label:'${name}', action:"refresh.refresh",
            icon:"st.doors.garage.garage-open", backgroundColor:"#ffa81e")
      state("closed", label:'${name}', action:"presetPosition",
            icon:"st.doors.garage.garage-closed", backgroundColor:"#bbbbdd",
            nextState:"partially open")
      state("open", label:'${name}', action:"close",
            icon:"st.doors.garage.garage-open", backgroundColor:"#ffcc33",
            nextState:"closed")
      state("partially open", label:'preset', action:"close",
            icon:"st.Transportation.transportation13",
            backgroundColor:"#ffcc33", nextState:"closed")
      state("tilt", label:'${name}', action:"close",
            icon:"st.doors.garage.garage-closed", backgroundColor:"#ddc288",
            nextState:"closed")
      state("closing", label:'${name}', action:"presetPosition",
            icon:"st.doors.garage.garage-closing", backgroundColor:"#bbbbdd")
      state("opening", label:'${name}', action:"presetPosition",
            icon:"st.doors.garage.garage-opening", backgroundColor:"#ffcc33")
    }

    standardTile("on", "device.switch", width: 2, height: 2,
                 inactiveLabel: false, decoration: "flat") {
      state("default", label:'open', action:"open",
            icon:"st.doors.garage.garage-opening")
    }
    standardTile("off", "device.switch", width: 2, height: 2,
                 inactiveLabel: false, decoration: "flat") {
      state("default", label:'close', action:"close",
            icon:"st.doors.garage.garage-closing")
    }
    standardTile("preset", "device.switch", width: 2, height: 2,
                 inactiveLabel: false, decoration: "flat") {
      state("default", label:'preset', action:"switch level.setLevel",
            icon:"st.Transportation.transportation13")
    }
    standardTile("tilt", "device.switch", width: 2, height: 2,
                 inactiveLabel: false, decoration: "flat") {
      state("default", label:'tilt', action:"lock.lock",
            icon:"st.doors.garage.garage-closed")
    }
    standardTile("refresh", "command.refresh", width:2, height:2,
                 inactiveLabel: false, decoration: "flat") {
      state("default", label:'', action:"refresh.refresh",
            icon:"st.secondary.refresh")
    }

    main "switchmain"
    details(["shade", "on", "off", "preset", "tilt"])
  }
}

def parse(String description) {
  log.debug "Parsing '${description}'"
}

def on() {
  log.trace "on() treated as presetPosition()"
  presetPosition()
}

def off() {
  log.trace "off() treated as close()"
  close()
}

def lock() {
  log.trace "lock() treated as tilt()"
  tilt(state.tiltLevel)
}

def setLevel() {
  log.trace "setLevel() treated as preset position"
  presetPosition()
}

def open() {
  log.trace "open()"
  sendEvent(name: "windowShade", value: "open")
  sendEvent(name: "switch", value: "on")
  move(65535, 1);
}

def close() {
  log.trace "close()"
  sendEvent(name: "windowShade", value: "closed")
  sendEvent(name: "switch", value: "off")  
  move(0, 1)
}

def presetPosition() {
  log.trace "presetPosition()"
  sendEvent(name: "windowShade", value: "partially open")
  sendEvent(name: "switch", value: "on")
  move(state.level * 65535 / 100, 1)
}

def refresh() {
  log.trace "refresh()"
}

def setLevel(level) {
  log.trace "setLevel(level)  $level"
  state.level = level
  sendEvent(name: "level", value: level)
  sendEvent(name: "windowShade", value: "open")
  sendEvent(name: "switch", value: "on")
  return move(level * 65535 / 100, 1)
}

def tilt(level) {
  log.debug "tilt $level"
  if (level < 0) level = 0
  if (level > 5) level = 5
  state.tiltLevel = level
  sendEvent(name: "tiltLevel", value: level)
  sendEvent(name: "windowShade", value: "tilt")
  sendEvent(name: "switch", value: "on")
  return move(level * 65535 / 10, 3)
}

private move(position, type) {
  log.debug "move $position type $type"
  def host = state.powerviewHubIP

  def headers = [:]
  headers.put("HOST", "$host:80")
  headers.put("Content-Type", "application/json")
  log.debug "The Header is $headers"

  def path = "/api/shades?shadeid=${state.blindId}"
  log.debug "path is: $path"

  def builder = new groovy.json.JsonBuilder()
  builder.shade {
     id state.blindId
     positions {
       position1 position
       posKind1 type
     }
  }

  try {
    def hubAction = new physicalgraph.device.HubAction(
      method: "PUT",
      path: path,
      body: builder.toString(),
      headers: headers)
    log.debug hubAction
    return hubAction
  } catch (Exception e) {
    log.debug "Hit Exception $e on $hubAction"
  }
}

def installed(){
  configure()
  state.blindId = device.name
  state.level = 50
  state.tiltLevel = 5
}

def updated(){
  configure()
}

private configure(){
  log.debug "config device ${device.name} hub ${parent.state.powerviewHubIP}"
  state.powerviewHubIP = parent.state.powerviewHubIP
}
