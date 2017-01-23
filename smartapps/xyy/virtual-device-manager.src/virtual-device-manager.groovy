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
  name: "Virtual Device Manager",
  namespace: "xyy",
  author: "KongQun Yang",
  description: "Create virtual devices to control physical devices",
  category: "My Apps",
  singleInstance: true,
  iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
  iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
  iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png"
)

preferences {
  page(name: "mainPage", title: "Installed Devices", install: true,
       uninstall: true) {
    section {
      app(name: "virtualDevice", appName: "Virtual Device Manager Child",
          namespace: "xyy", title: "New Virtual Device", multiple: true)
    }
  }
}

def installed() {
  log.debug "Installed"
  initialize()
}

def updated() {
  log.debug "Updated"
  unsubscribe()
  initialize()
}

def initialize() {
}
