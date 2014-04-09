class UrlMappings {

    static mappings = {

        "/api/person"(resources: "person")
        "/api/institution"(resources: "institution")
        "/api/device"(resources: "device")
        "/api/deviceType"(resources: "deviceType")

        "/"(view: "/index")
        "500"(view: '/error')
    }
}
