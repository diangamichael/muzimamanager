package com.muzima

class Person {

    String identifier
    String gender
    Date birthdate

    static hasMany = [personNames: PersonName, personAddresses: PersonAddress, devices: Device]

    static belongsTo = [institution: Institution]

    static constraints = {
        identifier nullable: false, blank: false
        gender nullable: false, blank: false
        birthdate nullable: false, max: new Date()
    }

    static mapping = {
        personNames sort: 'familyName', order: 'asc'
        personAddresses sort: 'address1', order: 'asc'
    }

    def PersonName getPersonName() {
        def preferredPersonName
        personNames.eachWithIndex {
            def personName, int i ->
                if (personName.preferred) {
                    preferredPersonName = personName
                }
        }
        if (preferredPersonName == null && !personNames.isEmpty()) {
            preferredPersonName = personNames.getAt(0)
        }
        return preferredPersonName;
    }

    def PersonAddress getPersonAddress() {
        def preferredPersonAddress
        personAddresses.eachWithIndex {
            def personAddress, int i ->
                if (personAddress.preferred) {
                    preferredPersonAddress = personAddress
                }
        }
        if (preferredPersonAddress == null && !personAddresses.isEmpty()) {
            preferredPersonAddress = personAddresses.getAt(0)
        }
        return preferredPersonAddress;
    }
}
