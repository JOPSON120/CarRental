package car.rental.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

@JsonDeserialize
public record CarConfiguration(@JsonProperty int limit, @JsonProperty("registrations") List<String> registrationNumbers) {
}
