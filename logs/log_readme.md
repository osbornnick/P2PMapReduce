This is janky, but this directory should exist for logging to work
creating a directory programatically is a bit weird with OSs (at least its messed up with me with Unix vs Windows)

So I'm slapping a markdown file in here, and telling the gitignore to ignore all text files in here. That way
this directory will also exist on your machines, but all our logs won't be committed to git.