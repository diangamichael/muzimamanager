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

    void updateAssignment(Assignment assignment) {
        this.person = assignment.person
        this.device = assignment.device

        this.voided = assignment.voided
        this.dateVoided = assignment.dateVoided
        this.voidedReason = assignment.voidedReason
    }
}
