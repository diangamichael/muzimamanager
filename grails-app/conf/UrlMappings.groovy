class UrlMappings {

    static mappings = {

        "/api/person"(resources: "person")
        "/api/institution"(resources: "institution")
        "/api/device"(resources: "device")
        "/api/deviceType"(resources: "deviceType")
        "/api/assignment"(resources: "assignment")

        "/"(view: "/index")
        "500"(view: '/error')
    }
}
