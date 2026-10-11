package com.codementor.backend.tutor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tutor")
public class TutorController {

    private final TutorService tutorService;

    public TutorController(TutorService tutorService) {
        this.tutorService = tutorService;
    }

    @PostMapping("/teach")
    public ResponseEntity<TutorResponseDTO> teach(
            @RequestBody TutorRequestDTO request
    ) {
        return ResponseEntity.ok(
                tutorService.teach(request)
        );
    }
}
