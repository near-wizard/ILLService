from graphics import *

def drawFace(center, size, win):
    """
    Draws a face at the specified center, with the size in the provided window.
    
    Args: # The functions we will be using within this program
        center (Point): The center of the face.
        size (int): The radius of the face.
        win (GraphWin): The window where the face will be drawn.
    """
    # Face (circle)
    face = Circle(center, size)
    face.setFill("blue")
    face.setOutline("green")
    face.draw(win)
    
    # Eyes
    eye_offset_x = 20
    eye_offset_y = 5
    eye_radius = 10
    left_eye = Circle(Point(center.getX() - eye_offset_x, center.getY() - eye_offset_y), eye_radius)
    left_eye.setFill("yellow")
    left_eye.draw(win)
    
    right_eye = Circle(Point(center.getX() + eye_offset_x, center.getY() - eye_offset_y), eye_radius)
    right_eye.setFill("yellow")
    right_eye.draw(win)
    
    # Mouth
    mouth_start = Point(center.getX() - size * 2, center.getY() + size * 5)
    mouth_end = Point(center.getX() + size * 2, center.getY() + size * 5)
    mouth = Line(mouth_start, mouth_end)
    mouth.setWidth(2)
    mouth.draw(win)

# Main Program to Test the drawFace Function
def main():
    # Create a graphical window
    win = GraphWin("Faces", 600, 600)
    win.setBackground("lightpink")
    
    # Draw faces of varying sizes
    drawFace(Point(150, 150), 50, win)  # Small face
    drawFace(Point(300, 300), 100, win)  # Medium face
    drawFace(Point(450, 450), 70, win)  # Another face of different size
    
    # Wait for user interaction to close
    win.getMouse()  # Pause to view result
    win.close()

# Run the main program
main()
