package com.muzima

import grails.transaction.Transactional

import static org.springframework.http.HttpStatus.CREATED
import static org.springframework.http.HttpStatus.NOT_FOUND
import static org.springframework.http.HttpStatus.NO_CONTENT
import static org.springframework.http.HttpStatus.OK

@Transactional(readOnly = true)
class InstitutionController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond Institution.list(params), model: [institutionInstanceCount: Institution.count()]
    }

    def show(Institution institutionInstance) {
        respond institutionInstance
    }

    def create() {
        respond new Institution(params)
    }

    @Transactional
    def save(Institution institutionInstance) {
        if (institutionInstance == null) {
            notFound()
            return
        }

        if (institutionInstance.hasErrors()) {
            respond institutionInstance.errors, view: 'create'
            return
        }

        institutionInstance.save flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'Institution.label', default: 'Institution'), institutionInstance.id])
                redirect institutionInstance
            }
            '*' { respond institutionInstance, [status: CREATED] }
        }
    }

    def edit(Institution institutionInstance) {
        respond institutionInstance
    }

    @Transactional
    def update(Institution institutionInstance) {
        if (institutionInstance == null) {
            notFound()
            return
        }

        if (institutionInstance.hasErrors()) {
            respond institutionInstance.errors, view: 'edit'
            return
        }

        institutionInstance.save flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'Institution.label', default: 'Institution'), institutionInstance.id])
                redirect institutionInstance
            }
            '*' { respond institutionInstance, [status: OK] }
        }
    }

    @Transactional
    def delete(Institution institutionInstance) {

        if (institutionInstance == null) {
            notFound()
            return
        }

        institutionInstance.delete flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'Institution.label', default: 'Institution'), institutionInstance.id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'Institution.label', default: 'Institution'), params.id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }
}
