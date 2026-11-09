const users = [
    // {
    //     username: "security-user",
    //     password: "security123",
    //     database: "security-db"
    // },
    {
        username: "event-user",
        password: "password",
        database: "events_db"
    },
    // {
    //     username: "payment-user",
    //     password: "payment123",
    //     database: "payment-db"
    // },
    // {
    //     username: "customer-user",
    //     password: "customer123",
    //     database: "customer-db"
    // },
    // {
    //     username: "saga-user",
    //     password: "saga123",
    //     database: "saga-db"
    // },
    // {
    //     username: "incentive-user",
    //     password: "incentive123",
    //     database: "incentive-db"
    // }
];

users.forEach(user => {
    const database = db.getSiblingDB(user.database);

    database.createUser({
        user: user.username,
        pwd: user.password,
        roles: [
            {
                role: "readWrite",
                db: user.database
            }
        ]
    });

    print(`Created user ${user.username} for ${user.database}`);
});