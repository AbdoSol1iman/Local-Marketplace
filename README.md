# Local Marketplace Backend

## UI references

- Figma: https://www.figma.com/design/wrqSNPDOn4xGY3ivw1ItlS/Untitled?node-id=0-1&p=f&t=hSeXirzZ7zWpaRUo-0
- Flutter client: https://github.com/Abdelrhmaneldeeb011-max/Local-Marketplace

## Frontend page integration map

Use `X-Customer-Id` header in dev mode (`spring.profiles.active=dev`) for customer-scoped endpoints.

| Flutter page | Backend endpoints |
|---|---|
| Login | `POST /api/auth/login` |
| Sign up | `POST /api/auth/register` |
| Forgot password | `POST /api/auth/forgot-password` |
| Home (shops/products/categories/search) | `GET /api/categories`, `GET /api/products`, `GET /api/products/search` |
| Cart | `GET /api/cart`, `PUT /api/cart/items`, `DELETE /api/cart/items/{productId}`, `DELETE /api/cart` |
| Checkout | `POST /api/cart/checkout` |
| Order confirmed / track shipping | `GET /api/orders/{orderId}`, `GET /api/shipping/order/{orderId}`, `GET /api/payments/order/{orderId}` |
| Orders list + filters | `GET /api/orders/me`, `GET /api/orders/me/status?status=...`, `PUT /api/orders/{orderId}/cancel` |
| Profile | `GET /api/customers/me`, `PUT /api/customers/me`, `PUT /api/customers/me/password`, `DELETE /api/customers/me` |
| Admin (stores) | `GET /api/admin/stores`, `POST /api/admin/stores`, `PUT /api/admin/stores/{storeId}/status?active=...`, `DELETE /api/admin/stores/{storeId}` |
| Admin (users) | `GET /api/admin/users`, `PUT /api/admin/users/{customerId}/block?blocked=...`, `DELETE /api/admin/users/{customerId}` |
