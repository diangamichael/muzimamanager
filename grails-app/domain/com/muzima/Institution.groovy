package com.muzima

class Institution {

    String name
    String description

    static hasMany = [persons: Person, devices: Device]

    static constraints = {
        name nullable: false, blank: false
        description nullable: false, blank: false
    }
}
