# ProfileFetcher
A tool which designed to easily fetch profile data from web-API(s) and use it in your Minecraft projects (BungeeCord, Velocity, Spigot, PaperSpigot, etc.)

## API Usage
First we need to declare an instance of ProfileFetcher somewhere in order to use its functions (becareful, don't call that each time because of performance cost, it is designed to declared once).
```java
ProfileFetcher api = ProfileFetcher.API();
```
Alternatively, you can customize your fetcher instance functionality by passing "FetcherOptions" as a parameter in constructor, you can build one easily in one line by calling chains of option setting functions, take a look at the example below:
```java
FetcherOptions options = FetcherOptions.create()
        .register(new RequestHandler<String, Textures>(RequestType.USERNAME_TO_PROFILE, username -> null, 2000))
        .setRequestExecutor(Executors.newFixedThreadPool(5))
        .shouldIgnoreErrors(true);

ProfileFetcher api = ProfileFetcher.API(options);
```
In this example we are registering a requester for (username to profile) which will do nothing, we also set an executor for completable futures to a fixed thread pool of 5 threads, and ignoring errors for sure, so if any exceptions thrown in the request functions, it will ignored and iterates to the next requester in order to fetch data.

Other usages of the api is pretty much straight-forward, but still, here is an example of setting player profile in PaperSpigot (note the setProperty function):
```java
api.fetchTexturesAsync("md_5").whenComplete((textures, ex) -> {
    Texture texture = textures.getMain();

    player.sendMessage(Component.text("Changed skin!"));
    player.getPlayerProfile().setProperty(new ProfileProperty("textures", texture.value, texture.signature));
    player.spigot().respawn();
});
```
