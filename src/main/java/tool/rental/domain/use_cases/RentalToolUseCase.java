package tool.rental.domain.use_cases;

import tool.rental.domain.entities.Friend;
import tool.rental.domain.entities.Rental;
import tool.rental.domain.entities.Tool;
import tool.rental.domain.repositories.RentalRepository;
import tool.rental.utils.ToastError;

public class RentalToolUseCase {
    private final RentalRepository rentalRepository = new RentalRepository();

    public void execute(Friend friend, Tool tool) throws ToastError {
        this.rentalRepository.updateRentalTimestamp(System.currentTimeMillis(), friend, tool);
    }
}
