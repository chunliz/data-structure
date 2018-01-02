public class Planet {
	public double xxPos;
	public double yyPos;
	public double xxVel;
	public double yyVel;
	public double mass;
	public String imgFileName;

	public Planet(double xP, double yP, double xV, double yV, double m, String img){
		xxPos=xP;
		yyPos=yP;
		xxVel=xV;
		yyVel=yV;
		mass=m;
		imgFileName=img;
	}
	public Planet(Planet p){
		xxPos=p.xxPos;
		yyPos=p.yyPos;
		xxVel=p.xxVel;
		yyVel=p.yyVel;
		mass=p.mass;
		imgFileName=p.imgFileName;
	}
	public double calcDistance(Planet p){
		double xxDiff=p.xxPos-xxPos;
		double yyDiff=p.yyPos-yyPos;
		double r=Math.sqrt(xxDiff*xxDiff+yyDiff*yyDiff);
		return r;
	}
	public double calcForceExertedBy(Planet p){
		double g=6.67e-11;
		double r=calcDistance(p);
		double f=g*mass*p.mass/(r*r);
		return f;
	}
	public double calcForceExertedByX(Planet p){
		double f=calcForceExertedBy(p);
		double r=calcDistance(p);
		return f*(p.xxPos-xxPos)/r;
	}
	public double calcForceExertedByY(Planet p){
		double f=calcForceExertedBy(p);
		double r=calcDistance(p);
		return f*(p.yyPos-yyPos)/r;
	}
	public double calcNetForceExertedByX(Planet[] allPlanets){
		double fx=0.0;
		for(Planet p : allPlanets){
			if(!this.equals(p)) fx+=calcForceExertedByX(p);
		}
		return fx;
	}
	public double calcNetForceExertedByY(Planet[] allPlanets){
		double fy=0.0;
		for(Planet p : allPlanets){
			if(!this.equals(p)) fy+=calcForceExertedByY(p);
		}
		return fy;
	}
	public void update(double dt, double fx, double fy){
		double ax=fx/mass;
		double ay=fy/mass;
		xxVel=xxVel+ax*dt;
		yyVel=yyVel+ay*dt;
		xxPos=xxPos+xxVel*dt;
		yyPos=yyPos+yyVel*dt;
		return;
	}
	public void draw(){
		StdDraw.picture(xxPos,yyPos,"images/"+imgFileName);
	}
}
