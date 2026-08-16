package mk.ukim.finki.searchindexing.integration.vezilka;

import lombok.extern.slf4j.Slf4j;
import mk.ukim.finki.searchindexing.model.enums.DonationStatus;
import mk.ukim.finki.searchindexing.model.exception.VezilkaIntegrationException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/**
 * REST-based implementation of the {@link VezilkaClient} seam.
 *
 * <p><b>Note:</b> doniraj.vezilka.ai does not publish a public API
 * specification, so the endpoint paths and payload shape below
 * ({@code POST /api/donations/text}, {@code GET /api/donations/{reference}})
 * are a reasonable REST convention rather than a confirmed contract. Adjust
 * {@link #DONATE_PATH} / {@link #STATUS_PATH} and the response field mapping
 * in {@link #toReceipt} / {@link #toStatus} once you have real API
 * credentials/docs from the platform or the course staff — the rest of the
 * application only depends on the {@link VezilkaClient} interface, so nothing
 * else needs to change.</p>
 */
@Component
@Slf4j
public class RestVezilkaClient implements VezilkaClient {
    private static final String DONATE_PATH = "/api/donations/text";
    private static final String STATUS_PATH = "/api/donations/{reference}";

    private final RestClient restClient;

    public RestVezilkaClient(VezilkaProperties vezilkaProperties) {
        String baseUrl = vezilkaProperties.baseUrl() == null
            ? "https://doniraj.vezilka.ai"
            : vezilkaProperties.baseUrl();

        RestClient.Builder builder = RestClient.builder().baseUrl(baseUrl);
        if (vezilkaProperties.apiKey() != null && !vezilkaProperties.apiKey().isBlank()) {
            builder.defaultHeader("Authorization", "Bearer " + vezilkaProperties.apiKey());
        }
        this.restClient = builder.build();
    }

    @Override
    public DonationReceipt submitTextDonation(TextDonationRequest request) {
        try {
            VezilkaDonationResponse response = restClient
                .post()
                .uri(DONATE_PATH)
                .body(request)
                .retrieve()
                .body(VezilkaDonationResponse.class);
            return toReceipt(response);
        } catch (RestClientException exception) {
            throw new VezilkaIntegrationException(
                "Failed to submit donation '" + request.title() + "' to doniraj.vezilka.ai: "
                    + exception.getMessage(),
                exception
            );
        }
    }

    @Override
    public DonationStatus checkStatus(String vezilkaReference) {
        try {
            VezilkaDonationResponse response = restClient
                .get()
                .uri(STATUS_PATH, vezilkaReference)
                .retrieve()
                .body(VezilkaDonationResponse.class);
            return toStatus(response);
        } catch (RestClientException exception) {
            throw new VezilkaIntegrationException(
                "Failed to check status of donation '" + vezilkaReference + "' on doniraj.vezilka.ai: "
                    + exception.getMessage(),
                exception
            );
        }
    }

    private DonationReceipt toReceipt(VezilkaDonationResponse response) {
        if (response == null) {
            throw new VezilkaIntegrationException("doniraj.vezilka.ai returned an empty response.");
        }
        String reference = response.reference() != null ? response.reference() : response.id();
        if (reference == null || reference.isBlank()) {
            throw new VezilkaIntegrationException("doniraj.vezilka.ai response did not include a reference id.");
        }
        return new DonationReceipt(reference, response.message());
    }

    private DonationStatus toStatus(VezilkaDonationResponse response) {
        if (response == null || response.status() == null) {
            return DonationStatus.SUBMITTED;
        }
        return switch (response.status().trim().toLowerCase()) {
            case "accepted", "approved", "success" -> DonationStatus.ACCEPTED;
            case "rejected", "declined" -> DonationStatus.REJECTED;
            case "failed", "error" -> DonationStatus.FAILED;
            default -> DonationStatus.SUBMITTED;
        };
    }

    /**
     * Loosely-typed response envelope: doniraj.vezilka.ai's actual field names
     * are unconfirmed, so both {@code reference} and {@code id} are accepted
     * for the donation identifier.
     */
    private record VezilkaDonationResponse(String reference, String id, String message, String status) {
    }
}
