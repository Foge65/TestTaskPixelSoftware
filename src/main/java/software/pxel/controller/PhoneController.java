package software.pxel.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import software.pxel.service.PhoneService;

@RestController
@RequestMapping("/api/phone")
@RequiredArgsConstructor
public class PhoneController {
    private final PhoneService phoneService;

    @PostMapping("/addPhone")
    public ResponseEntity<?> addPhone(@RequestParam String newPhone) {
        phoneService.addPhone(newPhone);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/deletePhone")
    public ResponseEntity<?> deletePhone(@RequestParam String phone) {
        phoneService.deletePhone(phone);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/changePhone")
    public ResponseEntity<?> changePhone(@RequestParam String oldPhone, @RequestParam String newPhone) {
        phoneService.changePhone(oldPhone, newPhone);
        return ResponseEntity.ok().build();
    }
}
