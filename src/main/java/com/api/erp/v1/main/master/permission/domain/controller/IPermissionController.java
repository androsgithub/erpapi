package com.api.erp.v1.main.master.permission.domain.controller;

import com.api.erp.v1.main.master.permission.application.dto.request.create.NewPermissionRequest;
import com.api.erp.v1.main.master.permission.application.dto.response.PermissionResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Set;

public interface IPermissionController {

    ResponseEntity<PermissionResponse> createPermission(
            @RequestBody NewPermissionRequest request
    );

    ResponseEntity<Set<PermissionResponse>> getAllPermissions();
}
