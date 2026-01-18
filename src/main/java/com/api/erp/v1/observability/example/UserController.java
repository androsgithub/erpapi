//package com.api.erp.v1.observability.example;
//
//import com.api.erp.v1.observability.application.annotation.TrackFlow;
//import com.api.erp.v1.observability.application.context.TraceContext;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
///**
// * Exemplo de Controller que usa serviços anotados com @TrackFlow.
// * Demonstra o fluxo completo de requisição com rastreamento automático.
// */
//@RestController
//@RequestMapping("/api/v1/users")
//public class UserController {
//
//    private final ExampleUserService userService;
//
//    public UserController(ExampleUserService userService) {
//        this.userService = userService;
//    }
//
//    /**
//     * POST /api/v1/users
//     *
//     * Fluxo de requisição:
//     * 1. TraceContextFilter extrai/gera X-Trace-Id
//     * 2. TraceContext.setTraceId() armazena em ThreadLocal
//     * 3. Recebe em controller
//     * 4. Chama userService.createUser()
//     * 5. AOP intercepta @TrackFlow("CREATE_USER")
//     * 6. Emite eventos START, SUCCESS/ERROR
//     * 7. Retorna resposta com X-Trace-Id
//     */
//    @PostMapping
//    public ResponseEntity<ExampleUserService.UserDTO> createUser(
//            @RequestBody ExampleUserService.CreateUserRequest request) {
//
//        String traceId = TraceContext.getTraceId();
//        System.out.println("Criando usuário com traceId: " + traceId);
//
//        ExampleUserService.UserDTO user = userService.createUser(request);
//
//        return ResponseEntity.ok(user);
//    }
//
//    /**
//     * GET /api/v1/users/{id}
//     */
//    @GetMapping("/{id}")
//    @TrackFlow("GET_USER")
//    public ResponseEntity<ExampleUserService.UserDTO> getUser(@PathVariable Long id) {
//        ExampleUserService.UserDTO user = new ExampleUserService.UserDTO();
//        user.setId(id);
//        user.setName("John Doe");
//        user.setEmail("john@example.com");
//
//        return ResponseEntity.ok(user);
//    }
//
//    /**
//     * PUT /api/v1/users/{id}
//     */
//    @PutMapping("/{id}")
//    public ResponseEntity<ExampleUserService.UserDTO> updateUser(
//            @PathVariable Long id,
//            @RequestBody ExampleUserService.UpdateUserRequest request) {
//
//        ExampleUserService.UserDTO user = userService.updateUser(id, request);
//        return ResponseEntity.ok(user);
//    }
//
//    /**
//     * DELETE /api/v1/users/{id}
//     */
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
//        userService.deleteUser(id);
//        return ResponseEntity.noContent().build();
//    }
//}
