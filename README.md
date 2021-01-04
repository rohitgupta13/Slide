# Slide Image Puzzle
<img src="https://blipthirteen.com/images/projects/slide/slide1.png" alt="screenshot" height="400" />


For a sliding puzzle to be solvable, **the number of swaps from the solved state should be even between the tiles**.

The snippet below shows a random generation for a 3x3 puzzle(aka 8 puzzle)
```
private void randomize(){
        int index = 0;
        switch (size){
            case 3: {
                // First arrange sequentially
                for (int i = 1120; i >= 480; i -= 320) {
                    for (int j = 60; j <= 700; j += 320) {
                        ImageButton imageButton = blockList.get(index++);
                        imageButton.setPosition(j,i);
                    }
                }
            }
            break;
            ...
        }
        // Set last block to 0,0 and hide it
        blockList.get(--index).setPosition(0,0);

        // Make even swaps and save the position of the swapped blocks in a set
        // limit = size * size;
        Set set = new HashSet();
        int swaps = 0;
        int swapLimit = (limit/2) - 2;
        while(swaps < swapLimit) {
            int rand1 = MathUtils.random(0, limit-2);
            int rand2 = MathUtils.random(0, limit-2);
            if (!set.contains(rand1) && !set.contains(rand2) && rand1 != rand2) {
                set.add(rand1);
                set.add(rand2);
                Vector2 swapPosition1 = new Vector2(blockList.get(rand1).getX(), blockList.get(rand1).getY());
                Vector2 swapPosition2 = new Vector2(blockList.get(rand2).getX(), blockList.get(rand2).getY());
                blockList.get(rand1).setPosition(swapPosition2.x, swapPosition2.y);
                blockList.get(rand2).setPosition(swapPosition1.x, swapPosition1.y);
                swaps++;
            }
        }
    }
```
