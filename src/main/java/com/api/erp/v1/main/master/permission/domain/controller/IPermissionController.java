package com.api.erp.v1.main.master.permission.domain.controller;

import com.api.erp.v1.main.master.permission.application.dto.request.CreatePermissionRequest;
import com.api.erp.v1.main.master.permission.application.dto.response.PermissionResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface IPermissionController {

    ResponseEntity<PermissionResponse> createPermission(
            @RequestBody CreatePermissionRequest request
    );

    ResponseEntity<List<PermissionResponse>> getAllPermissions();
}
