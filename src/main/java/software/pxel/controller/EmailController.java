package software.pxel.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import software.pxel.service.EmailService;

@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor
public class EmailController {
    private final EmailService emailService;

    @PostMapping("/addEmail")
    public ResponseEntity<?> addEmail(@RequestParam String newEmail) {
        emailService.addEmail(newEmail);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/deleteEmail")
    public ResponseEntity<?> deleteEmail(@RequestParam String email) {
        emailService.deleteEmail(email);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/changeEmail")
    public ResponseEntity<?> changeEmail(@RequestParam String oldEmail, @RequestParam String newEmail) {
        emailService.changeEmail(oldEmail, newEmail);
        return ResponseEntity.ok().build();
    }
}
