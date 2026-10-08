# Software Requirements Specification (SRS) - FleetTrackPro

## 1. Introduction
FleetTrackPro is a mobile courier logistics management app for Cirebon delivery operations.

## 2. Functional Requirements (SRS-FTP-01 to SRS-FTP-05)

### SRS-FTP-01: Offline-Ready Manifest Display
- **Description**: The app shall display daily delivery manifests from local cache instantly without requiring active network connectivity.
- **Acceptance Criteria**: App launches and displays seed orders even when device has no internet connection.

### SRS-FTP-02: Delivery Status Badging
- **Description**: Each delivery order card shall display its status (`PENDING`, `IN_DELIVERY`, `DELIVERED`, `FAILED`) with clear visual color badges.
- **Acceptance Criteria**: Amber for PENDING, Blue for IN_DELIVERY, Green for DELIVERED, Red for FAILED.

### SRS-FTP-03: Instant Local Status Update & Sync Queue
- **Description**: Couriers can mark an order as delivered. The change is stored instantly in the local database and reflected in the UI without waiting for server confirmation.
- **Acceptance Criteria**: Tapping "Deliver" updates status to DELIVERED immediately, queues change in `sync_queue`, and updates UI reactively.

### SRS-FTP-04: Swipe-to-Refresh & Synchronization
- **Description**: Pull-to-refresh fetches remote manifest, caches results locally, and processes pending sync queue items.
- **Acceptance Criteria**: Swipe gesture triggers refresh animation, attempts network fetch/sync, and preserves local cache on network failure.

### SRS-FTP-05: Instant Offline Search
- **Description**: Real-time search filter matching tracking number, recipient name, or destination address.
- **Acceptance Criteria**: Typing in search box filters results instantly offline; clearing search restores full manifest.
