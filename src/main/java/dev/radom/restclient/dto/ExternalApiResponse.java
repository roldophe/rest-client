package dev.radom.restclient.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExternalApiResponse {
  private Long id;
  private String name;
  private String email;
  private String message;
  private String status;
  private LocalDateTime createdAt;
}
