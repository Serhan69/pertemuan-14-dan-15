# Data Model - FleetTrackPro

## 1. Entities

### `DeliveryOrderEntity` (Table: `delivery_orders`)
- `orderId` (`order_id`): String (Primary Key)
- `trackingNumber` (`tracking_number`): String (Unique Index)
- `recipientName` (`recipient_name`): String
- `recipientPhone` (`recipient_phone`): String
- `destinationAddress` (`destination_address`): String
- `codAmount` (`cod_amount`): Double (Default `0.0`)
- `deliveryStatus` (`delivery_status`): String (`PENDING`, `IN_DELIVERY`, `DELIVERED`, `FAILED`)
- `failureReason` (`failure_reason`): String?
- `isSynced` (`is_synced`): Boolean
- `updatedAt` (`updated_at`): Long

### `SyncQueueEntity` (Table: `sync_queue`)
- `queueId` (`queue_id`): Long (Primary Key, AutoGenerate)
- `orderId` (`order_id`): String
- `targetStatus` (`target_status`): String
- `notes` (`notes`): String?
- `timestamp` (`timestamp`): Long

## 2. Initial Seed Data
When database is empty, 3 default orders are seeded:
1. `ORD-2026-8801` | `EXP-90021-JKT` | Ibu Ratna Dewi | 081234567890 | Jl. Melati Raya No. 45, Harjamukti, Cirebon | 125,000 | PENDING
2. `ORD-2026-8802` | `EXP-90022-BDG` | Pak Bambang Sutrisno | 081398765432 | Jl. Kartini No. 12, Kejaksan, Cirebon | 0 | IN_DELIVERY
3. `ORD-2026-8803` | `EXP-90023-CRB` | Sdr. Dimas Prasetyo | 085712349988 | Perum Citra Garden Blok B-3, Kedawung | 75,000 | DELIVERED
