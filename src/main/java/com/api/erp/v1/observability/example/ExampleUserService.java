//package com.api.erp.v1.observability.example;
//
//import com.api.erp.v1.observability.application.annotation.TrackFlow;
//import org.springframework.stereotype.Service;
//
///**
// * Exemplo de uso do módulo de observabilidade.
// * Demonstra como instrumentar métodos com @TrackFlow.
// */
//@Service
//public class ExampleUserService {
//
//    /**
//     * Exemplo: criar usuário com rastreamento automático.
//     *
////     * Fluxo:
//     * 1. AOP intercepta a chamada
//     * 2. Emite evento START
//     * 3. Executa o método
//     * 4. Emite evento SUCCESS com tempo de execução
//     *
//     * Se uma exceção for lançada:
//     * 1. AOP mapeia a exceção para FlowStatus
//     * 2. Emite evento ERROR_* com tempo de execução
//     */
//    @TrackFlow("CREATE_USER")
//    public UserDTO createUser(CreateUserRequest request) {
//        // Validar
//        if (request.getEmail() == null || request.getEmail().isBlank()) {
//            throw new IllegalArgumentException("Email é obrigatório");
//        }
//
//        // Processar
//        UserDTO user = new UserDTO();
//        user.setId(1);
//        user.setName(request.getName());
//        user.setEmail(request.getEmail());
//
//        return user;
//    }
//
//    /**
//     * Exemplo: atualizar usuário com rastreamento.
//     */
//    @TrackFlow("UPDATE_USER")
//    public UserDTO updateUser(Long userId, UpdateUserRequest request) {
//        // Buscar usuário no banco (pode falhar com DataAccessException)
//        UserDTO user = findUserById(userId);
//
//        // Atualizar
//        user.setName(request.getName());
//
//        return user;
//    }
//
//    /**
//     * Exemplo: deletar usuário.
//     * Sucesso: emite evento SUCCESS
//     */
//    @TrackFlow("DELETE_USER")
//    public void deleteUser(Long userId) {
//        // Simular exclusão
//        System.out.println("Deletando usuário: " + userId);
//    }
//
//    private UserDTO findUserById(Long userId) {
//        UserDTO user = new UserDTO();
//        user.setId(userId);
//        user.setName("John Doe");
//        user.setEmail("john@example.com");
//        return user;
//    }
//
//    // DTOs
//    public static class UserDTO {
//        private Long id;
//        private String name;
//        private String email;
//
//        public Long getId() { return id; }
//        public void setId(Long id) { this.id = id; }
//        public String getName() { return name; }
//        public void setName(String name) { this.name = name; }
//        public String getEmail() { return email; }
//        public void setEmail(String email) { this.email = email; }
//    }
//
//    public static class CreateUserRequest {
//        private String name;
//        private String email;
//
//        public String getName() { return name; }
//        public void setName(String name) { this.name = name; }
//        public String getEmail() { return email; }
//        public void setEmail(String email) { this.email = email; }
//    }
//
//    public static class UpdateUserRequest {
//        private String name;
//
//        public String getName() { return name; }
//        public void setName(String name) { this.name = name; }
//    }
//}
