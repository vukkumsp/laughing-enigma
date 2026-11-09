
const databases = {
    // security_db: [
    //     "users",
    //     "roles",
    //     "refresh_tokens"
    // ],

    // customer_db: [
    //     "customers",
    //     "customer-addresses"
    // ],

    events_db: [
        "event-details",
        "venue-details"
    ],

    // payment_db: [
    //     "payments",
    //     "payment-transactions"
    // ],

    // saga_db: [
    //     "saga-instances",
    //     "saga-events"
    // ],

    // notification_db: [
    //     "notifications",
    //     "email-templates"
    // ]
};

Object.entries(databases).forEach(([dbName, collections]) => {
    const database = db.getSiblingDB(dbName);

    collections.forEach(collectionName => {
        if (!database.getCollectionNames().includes(collectionName)) {
            database.createCollection(collectionName);
            print(`Created ${dbName}.${collectionName}`);
        }
    });
});
