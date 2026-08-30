/*
 *   Copyright 2026 RegadPoleCN
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

import { FursuitTvSdk, fursuitTvSdk, SdkLogLevel } from "@regadpole/fursuit-tv-sdk";

async function main() {
    let sdk: FursuitTvSdk | undefined;
    try {
        sdk = await fursuitTvSdk((config) => {
            config.clientId = "vap_xxxxxxxxxxxxxxxx";
            config.clientSecret = "your-client-secret-here";
            config.logLevel = SdkLogLevel.INFO;
        });

        console.log("SDK initialized successfully");

        const health = await sdk.base.health();
        console.log(`Health: ${health.message}`);

        const profile = await sdk.user.getUserProfile("username");
        console.log(`User: ${profile.nickname}`);

        const popular = await sdk.search.getPopular();
        console.log(`Popular users: ${popular.users.asJsReadonlyArrayView().length}`);
    } catch (error: unknown) {
        const msg = error instanceof Error ? error.message : String(error);
        console.error(`Error: ${msg}`);
    } finally {
        if (sdk) {
            sdk.close();
            console.log("SDK closed");
        }
    }
}

main();
