import { inject, Service } from '@angular/core';
import { Payment } from './payment';

interface RazorpayPaymentResponse {
    razorpay_order_id: string;
    razorpay_payment_id: string;
    razorpay_signature: string;
}

@Service()
export class PaymentCheckout {
    
    private readonly paymentService = inject(Payment);

    openCheckout(payment: {
        registrationId: string;
        eventId: string;
        orderId: string;
        amount: number;
        currency: string;
    }, callbacks?: {
        onPaymentFailed?: () => void;
    }): void {

        console.log(
            '[payment-checkout.ts] Opening Razorpay checkout with payment details:',
            payment
        );

        const options = {

            key: 'rzp_test_TT6t5xEgSnNOk2',

            amount: payment.amount * 100,

            currency: payment.currency,

            name: 'Laughing Enigma',

            description: 'Registration Payment',

            order_id: payment.orderId,

            retry: {
                enabled: false
            },

            handler: (response: RazorpayPaymentResponse) => {

                console.log(
                    'Razorpay payment response:',
                    response
                );

                if (!response.razorpay_order_id ||
                    !response.razorpay_payment_id ||
                    !response.razorpay_signature) {
                    console.error('Razorpay returned an incomplete success response:', response);
                    callbacks?.onPaymentFailed?.();
                    return;
                }

                this.paymentService.verifyPayment(payment.registrationId, payment.eventId, response.razorpay_order_id, response.razorpay_payment_id, response.razorpay_signature);
            }
            ,
            modal: {
                ondismiss: () => {
                    console.log('Razorpay checkout dismissed');
                }
            }
        };

        const razorpay = new window.Razorpay(options);

        razorpay.on('payment.failed', (response: { error: unknown }) => {
            console.error('Razorpay payment failed:', response.error);
            callbacks?.onPaymentFailed?.();
        });

        razorpay.open();
    }
}
