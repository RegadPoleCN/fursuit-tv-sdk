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
