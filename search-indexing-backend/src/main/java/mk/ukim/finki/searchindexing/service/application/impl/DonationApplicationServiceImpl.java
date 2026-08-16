package mk.ukim.finki.searchindexing.service.application.impl;

import java.util.List;
import java.util.Optional;
import mk.ukim.finki.searchindexing.model.dto.CreateDonationBatchDto;
import mk.ukim.finki.searchindexing.model.dto.DisplayDonationBatchDto;
import mk.ukim.finki.searchindexing.service.application.DonationApplicationService;
import mk.ukim.finki.searchindexing.service.domain.DonationService;
import org.springframework.stereotype.Service;

@Service
public class DonationApplicationServiceImpl implements DonationApplicationService {
    private final DonationService donationService;

    public DonationApplicationServiceImpl(DonationService donationService) {
        this.donationService = donationService;
    }

    @Override
    public List<DisplayDonationBatchDto> findAll() {
        return DisplayDonationBatchDto.from(donationService.findAll());
    }

    @Override
    public Optional<DisplayDonationBatchDto> findById(Long id) {
        return donationService.findById(id).map(DisplayDonationBatchDto::from);
    }

    @Override
    public DisplayDonationBatchDto create(CreateDonationBatchDto createDonationBatchDto) {
        return DisplayDonationBatchDto.from(donationService.createBatch(createDonationBatchDto.documentIds()));
    }

    @Override
    public DisplayDonationBatchDto approve(Long id) {
        return DisplayDonationBatchDto.from(donationService.approve(id));
    }

    @Override
    public DisplayDonationBatchDto submit(Long id) {
        return DisplayDonationBatchDto.from(donationService.submit(id));
    }
}
