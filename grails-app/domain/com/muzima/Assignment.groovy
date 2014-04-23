package com.muzima

class Assignment {

    Person person
    Device device

    Boolean voided = false
    Date dateVoided
    String voidedReason

    static constraints = {
        person nullable: false
        device nullable: false

        voided nullable: false
        dateVoided nullable: true
        voidedReason nullable: true
    }
}
