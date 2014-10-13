class UrlMappings {

    static mappings = {
        "/api/user"(resources: "user", excludes: ['create', 'edit', 'delete'])
        "/api/person"(resources: "person", excludes: ['create', 'edit', 'delete'])
        "/api/institution"(resources: "institution", excludes: ['create', 'edit', 'delete'])
        "/api/device"(resources: "device", excludes: ['create', 'edit', 'delete'])
        "/api/deviceType"(resources: "deviceType", excludes: ['create', 'edit', 'delete'])
        "/api/assignment"(resources: "assignment", excludes: ['create', 'edit', 'delete'])
        "/api/deviceLog"(resources: "deviceLog", excludes: ['create', 'edit', 'delete', 'update'])
        "/api/message"(resources: 'message', includes: ['index', 'save'])

        "/"(view: "/index")
        "500"(view: '/error')
    }
}
