/*
public class PCanvas extends View implements PViewInterface {

    public PorterDuff.Mode FILTER_ADD = PorterDuff.Mode.ADD;
    public PorterDuff.Mode FILTER_XOR = PorterDuff.Mode.XOR;
    public PorterDuff.Mode FILTER_CLEAR = PorterDuff.Mode.CLEAR;
    public PorterDuff.Mode FILTER_LIGHTEN = PorterDuff.Mode.LIGHTEN;
    public PorterDuff.Mode FILTER_MULTIPLY = PorterDuff.Mode.MULTIPLY;
    public PorterDuff.Mode FILTER_SCREEN = PorterDuff.Mode.SCREEN;
    public PorterDuff.Mode FILTER_OVERLAY = PorterDuff.Mode.OVERLAY;
    public PorterDuff.Mode FILTER_DARKEN = PorterDuff.Mode.DARKEN;
    public PorterDuff.Mode FILTER_DST = PorterDuff.Mode.DST;
    public PorterDuff.Mode FILTER_DST_ATOP = PorterDuff.Mode.DST_ATOP;
    public PorterDuff.Mode FILTER_DST_IN = PorterDuff.Mode.DST_IN;
    public PorterDuff.Mode FILTER_DST_OUT = PorterDuff.Mode.DST_OUT;
    public PorterDuff.Mode FILTER_DST_OVER = PorterDuff.Mode.DST_OVER;
    public PorterDuff.Mode FILTER_SRC = PorterDuff.Mode.SRC;
    public PorterDuff.Mode FILTER_SRC_ATOP = PorterDuff.Mode.SRC_ATOP;
    public PorterDuff.Mode FILTER_SRC_IN = PorterDuff.Mode.SRC_IN;
    public PorterDuff.Mode FILTER_SRC_OUT = PorterDuff.Mode.SRC_OUT;
    public PorterDuff.Mode FILTER_SRC_OVER = PorterDuff.Mode.SRC_OVER;

    public TileMode MODE_CLAMP = Shader.TileMode.CLAMP;
    public TileMode MODE_MIRROR = TileMode.MIRROR;
    public TileMode MODE_REPEAT = TileMode.REPEAT;



    private PLooper loop;
    private Paint mPaintFill;
    private Paint mPaintStroke;
    private Paint mPaintBackground;

    private final Rect textBounds = new Rect();

    public PCanvas(AppRunner appRunner) {
        super(appRunner.getAppContext());
        mAppRunner = appRunner;
    }


    public interface PCanvasInterfaceDraw {
        void onDraw(Canvas c);
    }

    public interface PCanvasInterfaceTouch {
        void onTouch(float x, float y);
    }

    private PCanvasInterfaceDraw pCanvasInterfaceDraw;
    private PCanvasInterfaceTouch pCanvasInterfaceTouch;
    private int currentLayer = -1;

    @Override
    protected void onDetachedFromWindow() {
        __stop();
        super.onDetachedFromWindow();
    }

    //on touch
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (pCanvasInterfaceTouch != null) {
            pCanvasInterfaceTouch.onTouch(event.getX(), event.getY());
        }
        return super.onTouchEvent(event);
    }

    public void draw(PCanvasInterfaceDraw pCanvasInterface) {
        pCanvasInterface.onDraw(mCanvasBuffer);
    }

    public void onTouch(PCanvasInterfaceTouch pCanvasInterfaceTouch) {
        this.pCanvasInterfaceTouch = pCanvasInterfaceTouch;
    }


    //TODO
    //
    //@APIMethod(description = "", example = "")
    //@APIParam(params = { "x", "y" })
    public PCanvas points(float[] points) {
        mCanvasBuffer.drawPoints(points, mPaintStroke);
        refresh();
        return this;
    }

    //TODO
    //
    //@APIMethod(description = "", example = "")
    //@APIParam(params = { "x", "y" })
    public PCanvas points(float[] points, int offset, int count) {
        mCanvasBuffer.drawPoints(points, offset, count, mPaintStroke);
        refresh();
        return this;
    }

    @ProtoMethod(description = "Draw a line", example = "")
    @ProtoMethodParam(params = {"x1", "y1", "x2", "y2"})
    public PCanvas line(float x1, float y1, float x2, float y2) {
        mCanvasBuffer.drawLine(x1, y1, x2, y2, mPaintStroke);
        refresh();
        return this;
    }

    //TODO
    //
    //@APIMethod(description = "", example = "")
    //@APIParam(params = { "x", "y" })
    public Path createPath(float[][] points, boolean close) {
        Path path = new Path();

        path.moveTo(points[0][0], points[0][1]);

        for (int i = 1; i < points.length; i++) {
            path.lineTo(points[i][0], points[i][1]);
        }
        if (close) {
            path.close();
        }

        return path;
    }

    @ProtoMethod(description = "Draw a path", example = "")
    @ProtoMethodParam(params = {"path"})
    public PCanvas path(Path path) {
        if (fillOn) mCanvasBuffer.drawPath(path, mPaintFill);
        if (strokeOn) mCanvasBuffer.drawPath(path, mPaintStroke);
        refresh();

        return this;
    }

    @ProtoMethod(description = "Set a dashed stroke", example = "")
    @ProtoMethodParam(params = {"float[]", "phase"})
    public PCanvas strokeDashed(float[] intervals, float phase) {

        // Stamp mContext concave arrow along the line
        PathEffect effect = new DashPathEffect(intervals, phase);
        mPaintStroke.setPathEffect(effect);

        return this;
    }


    public void drawTextCentered(String text){
        int cx = mCanvasBuffer.getWidth() / 2;
        int cy = mCanvasBuffer.getHeight() / 2;

        mPaintFill.getTextBounds(text, 0, text.length(), textBounds);
        mCanvasBuffer.drawText(text, cx - textBounds.exactCenterX(), cy - textBounds.exactCenterY(), mPaintFill);
    }


//    @ProtoMethod(description = "Sets a stroke join", example = "")
//    @ProtoMethodParam(params = {"join"})
//    public PCanvas strokeJoin(Paint.Join join) {
//        mPaintStroke.setStrokeJoin(Paint.Join.ROUND);
//
//        return this;
//    }

    @ProtoMethod(description = "Sets a given font", example = "")
    @ProtoMethodParam(params = {"typeface"})
    public PCanvas font(Typeface typeface) {
        mPaintFill.setTypeface(typeface);
        mPaintStroke.setTypeface(typeface);

        return this;
    }


    public PCanvas textType() {
        mPaintFill.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        mPaintStroke.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));

        return this;
    }



    @ProtoMethod(description = "", example = "")
    @ProtoMethodParam(params = {"filter"})
    public PCanvas filter(PorterDuff.Mode cornerMode) {
        mPaintFill.setXfermode(new PorterDuffXfermode(cornerMode));
        mPaintStroke.setXfermode(new PorterDuffXfermode(cornerMode));
        return this;
    }



    @ProtoMethod(description = "Create a bitmap shader", example = "")
    @ProtoMethodParam(params = {"bitmap", "tileMode"})
    public Shader createBitmapShader(Bitmap bitmap, TileMode cornerMode) {
        BitmapShader shader = new BitmapShader(bitmap, cornerMode, cornerMode);
        return shader;
    }



    @ProtoMethod(description = "Create a linear shader", example = "")
    @ProtoMethodParam(params = {"x1", "y1", "x2", "y2", "ArrayColorHex", "ArrayPositions", "tileMode"})
    public Shader linearShader(float x1, float y1, float x2, float y2, String[] colorsStr, float[] positions, TileMode cornerMode) {
        int colors[] = new int[colorsStr.length];
        for (int i = 0; i < colors.length; i++) {
            colors[i] = Color.parseColor(colorsStr[i]);
        }
        Shader shader = new LinearGradient(x1, y1, x2, y2, colors, positions, cornerMode);
        return shader;
    }

    @ProtoMethod(description = "Creates a sweep shader", example = "")
    @ProtoMethodParam(params = {"x", "y", "colorHex", "colorHex"})
    public Shader sweepShader(int x, int y, String c1, String c2) {
        Shader shader = new SweepGradient(x, y, Color.parseColor(c1), Color.parseColor(c2));
        return shader;
    }

    @ProtoMethod(description = "Compose two shaders", example = "")
    @ProtoMethodParam(params = {"shader1", "shader2", "cornerMode"})
    public Shader composeShader(Shader s1, Shader s2, PorterDuff.Mode cornerMode) {
        Shader shader = new ComposeShader(s1, s2, cornerMode);
        return shader;
    }



    //WARNING this method is experimental, be careful!
    //stretching parameter resize the textures
    public synchronized void size (int w, int h, boolean stretching) {
//        this.getLayoutParams().width = w;
//        this.getLayoutParams().height = h;

        ViewGroup.LayoutParams params = this.getLayoutParams();
        params.width = w;
        params.height = h;
        mWidth = w;
        mHeight = h;
        this.requestLayout();

        //reinit textures
        if (stretching) {
            currentLayer = -1;
            initLayers();
        }
    }

    public void __stop() {
        if (loop != null) {
            loop.stop();
        }
    }
}

*/