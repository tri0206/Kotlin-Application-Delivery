# 🚚 Delivery App (Android - Kotlin)

This is the Android mobile application for the Delivery App, built with Kotlin and designed for **three user roles**: **Customers**, **Restaurants**, and **Delivery Personnel**. The app includes real-time tracking, online payments, push notifications, and role-based UI/UX.

## 👥 Target Users

- **Customers**: Browse restaurants, place orders, make payments, and track deliveries in real-time.
- **Restaurants**: Manage incoming orders, update order statuses, and communicate with delivery personnel.
- **Delivery Shippers**: Receive delivery requests, navigate using Google Maps, and update delivery progress.

---

## 🚀 Technologies Used

- **Kotlin** — Main language for Android app development.
- **Firebase** — User authentication, Firestore database, Cloud Messaging (push notifications).
- **Google Maps API** — Real-time map navigation and shipper tracking.
- **Socket.IO** — WebSocket for real-time order and location tracking.
- **ZaloPay SDK** — Integrated online payment system for customer orders.
- **MVVM Architecture** — Maintainable and testable code structure.
- **Retrofit** — For networking and communicating with backend APIs.

---

## 📱 Features

### For Customers:
- View nearby restaurants and menus.
- Place orders with item customization.
- Real-time delivery tracking via map.
- Secure checkout with ZaloPay.

### For Restaurants:
- Real-time notifications of new orders.
- Dashboard to manage current and completed orders.
- Order detail view with status update control.

### For Delivery Drivers:
- View assigned deliveries and order details.
- Navigate using Google Maps.
- Live update location to customers and restaurants via WebSocket.
- Mark order as picked up and delivered.

---

## ⚙️ Project Structure (Modules)

```
app/
├── activities/
├── fragments/
├── viewmodels/
├── adapters/
├── models/
├── services/          # Firebase, Location, Notification
├── utils/
├── network/           # Retrofit API interfaces
├── socket/            # Socket.IO integration
└── MainApplication.kt
```

---

## 🛠️ Getting Started

### 1. Prerequisites

- Android Studio Giraffe or newer
- Firebase project setup (Authentication, Firestore, FCM)
- Google Maps API key
- ZaloPay developer credentials

### 2. Clone the Project

```bash
git clone https://https://github.com/tri0206/Kotlin-Application-Delivery.git
```

### 3. Configure API Keys

Set your credentials in `local.properties` or `res/values/secrets.xml`:

```xml
<string name="google_maps_key">YOUR_GOOGLE_MAPS_API_KEY</string>
<string name="zalopay_app_id">YOUR_ZALOPAY_APP_ID</string>
```

### 4. Run the App

Open the project in Android Studio and run on emulator or physical device.

---

## 🔐 Authentication & Roles

Using Firebase Authentication with role-based access stored in Firestore. After login, the app redirects based on user type:

- `customer`
- `restaurant`
- `shipper`

---

## 📡 Real-time Tracking

Socket.IO is used for:
- Sending live shipper location to the backend and other clients
- Receiving real-time order status updates

Firebase Cloud Messaging is used for push notifications on:
- New order alerts
- Status changes
- Delivery updates

---

## 💳 ZaloPay Integration

ZaloPay SDK is used to:
- Initiate payment requests from the app
- Handle payment result callbacks
- Confirm transactions with backend

---

