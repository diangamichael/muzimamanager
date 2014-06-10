package com.muzima

class DeviceLog {

    String log;

    static belongsTo = [device: Device]

    static constraints = {
        log nullable: false, blank: false
    }
}
