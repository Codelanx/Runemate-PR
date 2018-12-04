Deprecate class-level for `deprecate.md`

`patch` describes direct replacement classes which can be dropped in

top-level package is new classes / new systems

`event` is some custom events that are spawned out of this. `ItemEvent` was split into `ItemAddedEvent` and `ItemRemovedEvent` for simplicity, but can be abused a little to transform old code via the code provided in `ItemEventHandler`

Some sample code provided in `/src/test/`