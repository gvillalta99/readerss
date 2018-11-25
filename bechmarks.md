# Results

## With pmap

### Not cached

lein run  34,69s user 2,03s system 35% cpu 1:44,38 total

### Cached

lein run  28,99s user 1,43s system 41% cpu 1:12,59 total

## With map

### Not cached

lein run  37,30s user 1,95s system 56% cpu 1:09,22 total

### Cached

lein run  28,29s user 1,40s system 42% cpu 1:09,37 total


## With (pmap with-feed-entries)

### Not cached

lein run  37,11s user 2,07s system 37% cpu 1:43,84 total

### Cached

lein run  29,19s user 1,56s system 40% cpu 1:15,05 total

## With mapv

### Not cached

lein run  35,29s user 1,95s system 54% cpu 1:08,05 total

### Cached

lein run  28,95s user 1,43s system 166% cpu 18,234 total


