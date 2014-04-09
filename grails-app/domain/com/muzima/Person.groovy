package com.muzima

class Person {

    String gender
    Date birthdate

    static hasMany = [personNames: PersonName, personAddresses: PersonAddress]

    static belongsTo = [institution: Institution]

    static constraints = {
        gender nullable: false, blank: false
        birthdate nullable: false, max: new Date()
    }

    static mapping = {
        personNames sort: 'familyName', order: 'asc'
        personAddresses sort: 'address1', order: 'asc'
    }
}
