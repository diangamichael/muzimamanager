package com.muzima

class DeviceType {

    String name
    String description

    static hasMany = [deviceDetails: DeviceDetail]

    static mapping = {
        description column: "description", sqlType: "varchar(1024)"
    }

    static constraints = {
        name nullable: false, blank: false
        description nullable: false, blank: false
    }

    void updateDeviceType(DeviceType deviceType) {
        this.name = deviceType.name
        this.description = deviceType.description
    }
}
