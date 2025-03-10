package com.yosri.defensy.backend.modules.user;

import org.springframework.modulith.ApplicationModule;

@ApplicationModule(allowedDependencies = {"org.springframework.context"}) // Only allows event publishing
public class UserModule {
}
