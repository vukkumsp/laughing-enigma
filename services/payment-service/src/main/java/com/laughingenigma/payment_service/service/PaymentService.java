package com.laughingenigma.payment_service.service;

import com.laughingenigma.payment_service.dto.PaymentOrderResponse;
import com.laughingenigma.payment_service.dto.PaymentRequest;
import com.laughingenigma.payment_service.dto.PaymentVerificationRequest;
import com.laughingenigma.payment_service.dto.PaymentVerificationResponse;
import com.laughingenigma.payment_service.entity.Payment;
import com.laughingenigma.payment_service.entity.PaymentStatus;
import com.laughingenigma.payment_service.repository.PaymentRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.razorpay.Utils;

/*  TODO:

    400 → invalid request
    404 → payment not found
    409 → conflicting payment state
    500 → actual server/provider problem

 */

@Service
public class PaymentService {

    private final RazorpayClient razorpayClient;
    private final PaymentRepository paymentRepository;

    @Value("${razorpay.key-secret}")
    private String keySecret;

    public PaymentService(
            RazorpayClient razorpayClient,
            PaymentRepository paymentRepository) {
        this.razorpayClient = razorpayClient;
        this.paymentRepository = paymentRepository;
    }

    public PaymentOrderResponse createOrder(PaymentRequest request) {

        try {
            long amountInPaise = request.amount()
                    .movePointRight(2)
                    .longValueExact();

            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amountInPaise);
            orderRequest.put("currency", "INR");
            orderRequest.put(
                    "receipt",
                    "customer_" + request.customerId()
            );

            // create Razorpay order
            Order order = razorpayClient.orders.create(orderRequest);

            // persist payment record
            Payment payment = Payment.builder()
                    .customerId(request.customerId())
                    .amount(request.amount())
                    .currency("INR")
                    .razorpayOrderId(order.get("id"))
                    .status(PaymentStatus.CREATED)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            paymentRepository.save(payment);

            // return order details
            return new PaymentOrderResponse(
                    null,
                    null,
                    order.get("id"),
                    request.amount(),
                    "INR",
                    order.get("status")
            );

        } catch (RazorpayException e) {
            throw new RuntimeException("Failed to create Razorpay order",
                    e);
        }
    }

    //verifyPayment()
    public PaymentOrderResponse getOrder(String orderId) {
        try {
            Order order = razorpayClient.orders.fetch(orderId);

            Number amount = order.get("amount");

            return new PaymentOrderResponse(
                    null,
                    null,
                    order.get("id"),
                    BigDecimal.valueOf(amount.longValue())
                            .movePointLeft(2),
                    order.get("currency"),
                    order.get("status")
            );

        } catch (RazorpayException e) {
            throw new RuntimeException(
                    "Failed to fetch Razorpay order",
                    e
            );
        }
    }


    public PaymentVerificationResponse verifyPayment(
            PaymentVerificationRequest request
    ) {
        System.out.println(request);
        try {

            String payload =
                    request.razorpayOrderId()
                            + "|"
                            + request.razorpayPaymentId();

            boolean valid = Utils.verifySignature(
                    payload,
                    request.razorpaySignature(),
                    keySecret
            );

            if (!valid) {
                throw new RuntimeException(
                        "Invalid payment signature"
                );
            }




            // We'll update the database here
            Payment payment = paymentRepository
                    .findByRazorpayOrderId(
                            request.razorpayOrderId()
                    )
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Payment order not found"
                            )
                    );

            if (payment.getRazorpayPaymentId() != null &&
                    !payment.getRazorpayPaymentId()
                            .equals(request.razorpayPaymentId())) {
                throw new RuntimeException(
                        "Payment ID does not match existing payment"
                );
            }

            if (payment.getStatus() == PaymentStatus.SUCCESS) {
                return new PaymentVerificationResponse(
                        payment.getRazorpayOrderId(),
                        payment.getRazorpayPaymentId(),
                        "SUCCESS"
                );
            }

            payment.setRazorpayPaymentId(
                    request.razorpayPaymentId()
            );
            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setPaidAt(LocalDateTime.now());
            payment.setUpdatedAt(LocalDateTime.now());

            paymentRepository.save(payment);

            return new PaymentVerificationResponse(
                    request.razorpayOrderId(),
                    request.razorpayPaymentId(),
                    "SUCCESS"
            );


        } catch (RazorpayException e) {
            throw new RuntimeException(
                    "Failed to verify payment",
                    e
            );
        }
    }
}
