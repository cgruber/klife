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
  - supports a start/stop button
  - supports a 'step' button for single manual stepping.
  - supports editing the field by clicking on it, toggling
    the bit on and off, while the simulation is paused.
  - Each window has its own separate world state, and it's
    easy to create a new window with the menu or ctrl-n.
  - lets you save and load "life" files - text files with a
    very simple format. Here's an example with a 100x80 space,
    a 7dp-per-square scale, and a glider.
    ```text
    ### Header Comment (ignored)
    size: 100,80
    scale: 7
    3,6
    4,6
    5,6
    5,5
    4,4
    ```
    - > Note loading is currently broken - it loads, but doesn't
        properly adjust the window, which then resizes the space,
        which then loses some of the space.
  - lets you load an image bitmap file (jpg/gif/png), doing a
    terrible job of interpolating a black and white on-and-off
    image.
      - The goal was to use pngs to store the bitmap, but I opted
        for a text format. Image loading remains as a neat gimick. 
      - Images loaded do not use "scale" but each pixel in the
        image is a bit in the space. Best used with very small
        images, such as icons.


Known issues:
  - Only tested on Mac
  - Resizing has bugs which can crash the app
    - it breaks the field if you pull the mouse smaller than
      a certain threshold (even with the minimum size constraints
      in place). If you go far enough, it starts throwing
      exceptions around negative padding numbers, etc.
    - I probably need to do something to force minimum sizes of
      internal components, so the window can't go smaller, but
      I couldn't figure that out (yet)

> Note: This is the first time I've used Compose, so it's
> probably terrible. I'm sure dispatching mutable state updates 
> from a Timer is the wrong move, but it was a way to generate
> 'ticks' so I went for it.
> 
> Also, it's partly factored out in a way that would let me
> evolve this into a more general home for other simulators,
> but I may not take it that far, so some maybe unnecessary
> abstractions are present.
> 
> Also also, I really need to make it so it's not storing the
> whole universe in a 2d array. For this purpose a sparse array
> would be sufficient, and given that's how my storage format
> works, I ought to fix the in-memory model.

