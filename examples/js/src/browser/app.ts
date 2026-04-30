import { FursuitTvSdk, fursuitTvSdk, SdkLogLevel } from "@regadpole/fursuit-tv-sdk";

const output = document.getElementById("output")!;

function log(message: string) {
    const line = document.createElement("p");
    line.textContent = message;
    output.appendChild(line);
}

async function main() {
    let sdk: FursuitTvSdk | undefined;
    try {
        sdk = await fursuitTvSdk((config) => {
            config.clientId = "vap_xxxxxxxxxxxxxxxx";
            config.clientSecret = "your-client-secret-here";
            config.logLevel = SdkLogLevel.INFO;
        });

        log("SDK initialized successfully");

        const health = await sdk.base.health();
        log(`Health: ${health.message}`);

        const profile = await sdk.user.getUserProfile("RegadPole");
        log(`User: ${profile.nickname}`);

        const popular = await sdk.search.getPopular();
        log(`Popular users: ${popular.users.length}`);
    } catch (error: unknown) {
        const msg = error instanceof Error ? error.message : String(error);
        log(`Error: ${msg}`);
    } finally {
        if (sdk) {
            sdk.close();
            log("SDK closed");
        }
    }
}

main();
