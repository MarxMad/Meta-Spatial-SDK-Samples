# Feature Proposal: 3D Object Scanner API Integration

## Description
This issue outlines the integration of a third-party Cloud Photogrammetry / NeRF API (such as Tripo3D, Luma AI, or Meshy) to enable real-world 3D object scanning and reconstruction within the RoomCast marketplace.

Since real-time photogrammetry calculations are resource-intensive for the Meta Quest 3 hardware, the system should adopt a hybrid client-server-cloud pipeline.

## Proposed Architecture
1. **Media Capture**: The user records a 360-degree video or captures multiple photos of a real object.
2. **Cloud Upload**: The app uploads the raw media (MP4/ZIP) to our backend server.
3. **API Processing**: The backend forwards the assets to the reconstruction service API (e.g., Tripo3D HD reconstruction).
4. **Dynamic Download & Placement**: Once the `.glb` file is compiled, our backend caches the model and notifies the Quest client. The Quest client downloads the asset dynamically at runtime and instances it in the Spatial SDK scene.

## Tasks & Implementation Steps
- [ ] Implement Retrofit/OkHttp client in RoomCast Android app.
- [ ] Refactor `placeInRoom(item: FurnitureItem)` to download and load `.glb` files dynamically from remote URLs instead of local assets directory.
- [ ] Connect the simulated `ObjectScannerSimulator` step 2 directly to our backend processing endpoint.
- [ ] Add loading indicators and progress listeners for dynamic asset downloads.
