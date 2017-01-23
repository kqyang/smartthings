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

definition(
  name: "Virtual Device Manager Child",
  namespace: "xyy",
  author: "KongQun Yang",
  description: "Virtual Device Manager child app to create new virtual devices.",
  category: "My Apps",
  iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
  iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
  iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png"
)


preferences {
  page(name: "mainPage", title: "New Virtual Device", install: true,
       uninstall: true) {
    section {
      input("deviceLabel", "text", title: "Device Label", required: true)
      input("switches", "capability.switch", multiple: true,
            title: "Select Switches", required: false)
      input("locks", "capability.lock", multiple: true, title: "Select Locks",
            required: false)
    }
  }
}

def installed() {
  log.debug "Installed with settings: ${settings}"
  initialize()
}

def updated() {
  log.debug "Updated with settings: ${settings}"
  unsubscribe()
  initialize()
}

def initialize() {
  app.updateLabel(deviceLabel)
  try {
    def devices = getChildDevices()
    if (devices) {
      devices.each {
        log.debug "configure device ${it.name}"
        it.configure()
      }
    } else {
      def deviceId = "xyy_virtual_${app.id}"
      addChildDevice("xyy", "Virtual Device", deviceId, null,
                     [name: deviceId, label: app.label, completedSetup: true])
    }
  } catch (e) {
    log.error "Error creating device: ${e}"
  }
}
