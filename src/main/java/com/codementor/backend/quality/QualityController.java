package com.codementor.backend.quality;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/quality")
public class QualityController {

    private final QualityService qualityService;

    public QualityController(QualityService qualityService) {
        this.qualityService = qualityService;
    }

    @PostMapping("/analyze")
    public ResponseEntity<QualityResponseDTO> analyzeQuality(
            @RequestBody QualityRequestDTO request
    ) {
        return ResponseEntity.ok(
                qualityService.analyzeQuality(request)
        );
    }
}
