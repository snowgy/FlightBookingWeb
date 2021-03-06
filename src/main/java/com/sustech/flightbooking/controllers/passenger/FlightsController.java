package com.sustech.flightbooking.controllers.passenger;

import com.sustech.flightbooking.controllers.ControllerBase;
import com.sustech.flightbooking.domainmodel.Flight;
import com.sustech.flightbooking.domainmodel.FlightStatus;
import com.sustech.flightbooking.domainmodel.Order;
import com.sustech.flightbooking.domainmodel.Passenger;
import com.sustech.flightbooking.persistence.FlightRepository;
import com.sustech.flightbooking.persistence.OrderRepository;
import com.sustech.flightbooking.services.FlightService;
import com.sustech.flightbooking.services.IdentityService;
import com.sustech.flightbooking.viewmodel.passenger.flight.FlightBookViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

@Controller
@RequestMapping("/passenger/flights")
public class FlightsController extends ControllerBase {

    private final FlightService flightService;
    private final FlightRepository flightRepository;
    private final IdentityService identityService;
    private final OrderRepository orderRepository;

    @Autowired
    public FlightsController(FlightRepository flightRepository, FlightService flightService,
                             IdentityService identityService, OrderRepository orderRepository) {
        this.flightService = flightService;
        this.flightRepository = flightRepository;
        this.identityService = identityService;
        this.orderRepository = orderRepository;
    }


    @GetMapping("book/{id}")
    public ModelAndView bookPage(@PathVariable UUID id) {
        Flight flight = flightRepository.findById(id);
        if (flight == null || flightService.getStatus(flight) != FlightStatus.AVAILABLE) {
            return notFound();
        }
        FlightBookViewModel vm = new FlightBookViewModel();
        vm.setFlightId(flight.getId());
        ModelAndView modelAndView = pageWithViewModel("passenger/flights/book", vm);
        ModelMap modelMap = modelAndView.getModelMap();
        modelMap.put("flight", flight);
        modelMap.put("passenger", identityService.getCurrentUser());
        modelMap.put("seats", flightService.getAvailableSeats(flight));
        return modelAndView;
    }

    @PostMapping("book")
    public ModelAndView book(@ModelAttribute FlightBookViewModel model) {
        Flight flight = flightRepository.findById(model.getFlightId());
        if (flight == null || flightService.getStatus(flight) != FlightStatus.AVAILABLE) {
            return notFound();
        }
        if (!flightService.getAvailableSeats(flight).contains(model.getSeat())) {
            return badRequest("Seat not available.");
        }
        Order order = new Order(UUID.randomUUID(),
                flight,
                (Passenger) identityService.getCurrentUser(),
                model.getSeat());
        orderRepository.save(order);
        return redirect(String.format("/passenger/orders/%s/pay", order.getId()));
    }
}
