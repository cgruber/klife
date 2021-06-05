# Klife

This is KLife, a Conway's Game of Life simulator, in a jetpack 
compose desktop window.

One goal was to explore Compose Desktop. A secondary goal was
to test out Bazel in this compose context. That hasn't yet
happened.


At present, it lets you
  - lets you scale the world (min 6 pixels, max 20 pixels per "bit")
  - lets you resize the window (and field)
  - implements a toroidal world topology 
  - implements standard Conway rules
  - Supports a start/stop button
  - Supports a 'step' button for single manual stepping.
  - Supports editing the field by clicking on it, toggling
    the bit on and off, while the simulation is paused.
  - lets you load a bitmap file, doing a terrible job of
    interpolating color into a black and white on-and-off
    image.
      - The goal was to use pngs to store the bitmap, since
        the field is essentially that, but I may back that
        out, and just store it in a compressed text-file
        or a proto or something.
      - Saving is not supported (yet)


Known issues:
  - Only tested on Mac
  - Resizing has bugs (see below) which can crash the app
  - No saving state

> The window resizing is weird - it breaks the field if you pull
> the mouse smaller than the resized window, while dragging the
> resize, and if you go far enough, starts throwing exceptions
> around negative padding numbers, etc. Something funky about
> resize and limits, etc.  IT sort of prevents too-small resizes,
> but it's hacky.

> Note: This is the first time I've used Compose, so it's
> probably terrible. I'm sure dispatching mutable state updates 
> from a Timer is the wrong move, but it was a way to generate
> 'ticks' so I went for it.

