const playStoreUrl = "https://play.google.com/store/apps/details?id=com.nexal.app";

function BrandMark() { return <span className="brand-mark" aria-hidden="true"><span /><span /></span>; }

function PlayButton({ compact = false }: { compact?: boolean }) {
  return <a className={`play-button ${compact ? "compact" : ""}`} href={playStoreUrl}><span className="play-triangle" aria-hidden="true" /><span><small>GET IT ON</small><strong>Google Play</strong></span></a>;
}

function PhonePreview() {
  return <div className="phone-wrap" aria-label="Nexal app dashboard preview">
    <div className="orbit orbit-one" /><div className="orbit orbit-two" />
    <div className="phone"><div className="phone-island" /><div className="phone-screen">
      <div className="app-topline"><div><span>GOOD MORNING</span><strong>Ready to move?</strong></div><i>N</i></div>
      <div className="daily-score"><div className="ring"><b>72</b><span>score</span></div><div><small>TODAY&apos;S PROGRESS</small><h3>On track</h3><p>1,640 of 2,200 kcal</p></div></div>
      <div className="mini-stats"><div><span>PROTEIN</span><strong>124g</strong><i style={{ "--fill": "82%" } as React.CSSProperties} /></div><div><span>STEPS</span><strong>7,842</strong><i style={{ "--fill": "67%" } as React.CSSProperties} /></div></div>
      <div className="next-card"><span>NEXT UP · AI WORKOUT</span><div><strong>Upper body strength</strong><b>32 min</b></div><p>6 exercises · Intermediate</p><button aria-label="Start workout">Start workout <em>→</em></button></div>
      <div className="week-bars" aria-hidden="true">{[48,70,55,88,64,82,76].map((height,index)=><div key={index}><i style={{height:`${height}%`}} /><span>{"SMTWTFS"[index]}</span></div>)}</div>
    </div></div>
    <div className="float-card float-meal"><i>✓</i><span><small>MEAL LOGGED</small><strong>Balanced lunch</strong></span></div>
    <div className="float-card float-streak"><strong>12</strong><span><small>DAY STREAK</small><b>Keep it going</b></span></div>
  </div>;
}

export default function Home() {
  return <main>
    <nav className="nav shell"><a href="#top" className="brand"><BrandMark /><strong>Nexal</strong></a><div className="nav-links"><a href="#features">Features</a><a href="#results">Results</a><a href="#premium">Premium</a></div><PlayButton compact /></nav>
    <section className="hero shell" id="top"><div className="hero-copy"><div className="eyebrow"><span /> YOUR FITNESS, FINALLY CONNECTED</div><h1>Build the body.<br /><em>Own the journey.</em></h1><p>Nexal brings your meals, workouts and progress into one intelligent daily plan—built around you, and designed to keep you moving.</p><div className="hero-actions"><PlayButton /><a className="text-link" href="#features">Explore Nexal <span>↓</span></a></div><div className="trust-row"><div className="avatars"><i>J</i><i>M</i><i>A</i><i>+</i></div><div><span>★★★★★</span><p>Built for real, lasting progress</p></div></div></div><PhonePreview /></section>
    <section className="signal-strip" aria-label="Nexal benefits"><div className="shell"><span>◆ PERSONALIZED DAILY TARGETS</span><span>◆ AI-POWERED PLANS</span><span>◆ MEANINGFUL PROGRESS</span></div></section>

    <section className="features-section shell" id="features">
      <div className="section-heading"><div><span className="section-kicker">ONE APP. YOUR WHOLE ROUTINE.</span><h2>Less guessing.<br />More <em>progress.</em></h2></div><p>Everything you need to train, eat and improve—connected in one clean daily experience.</p></div>
      <div className="feature-grid">
        <article className="feature-card dark-card"><span className="card-number">01</span><div className="feature-icon target-icon"><i /></div><h3>Track what matters</h3><p>Log meals, calories, macros and workouts without turning your life into a spreadsheet.</p><div className="macro-visual"><div><span>Protein</span><b>82%</b><i style={{"--bar":"82%"} as React.CSSProperties}/></div><div><span>Carbs</span><b>64%</b><i style={{"--bar":"64%"} as React.CSSProperties}/></div><div><span>Fats</span><b>71%</b><i style={{"--bar":"71%"} as React.CSSProperties}/></div></div></article>
        <article className="feature-card lime-card"><span className="card-number">02</span><div className="feature-icon sparkle-icon">✦</div><h3>Plans that think ahead</h3><p>Premium AI creates workout and meal plans around your goals, preferences and schedule.</p><div className="plan-stack"><div><i>MON</i><span><small>STRENGTH</small><b>Upper body</b></span><em>32 min</em></div><div><i>TUE</i><span><small>RECOVERY</small><b>Mobility flow</b></span><em>18 min</em></div><div><i>WED</i><span><small>STRENGTH</small><b>Lower body</b></span><em>40 min</em></div></div></article>
        <article className="feature-card light-card"><span className="card-number">03</span><div className="feature-icon chart-icon"><i/><i/><i/></div><h3>See the full picture</h3><p>Watch consistency turn into momentum with simple trends you can actually understand.</p><div className="spark-chart"><span>4 week trend</span><strong>+18%</strong><div className="chart-line"><i/><i/><i/><i/><i/><i/><i/><i/></div></div></article>
      </div>
    </section>

    <section className="results-section" id="results"><div className="shell results-grid">
      <div className="results-copy"><span className="section-kicker light">BUILT FOR CONSISTENCY</span><h2>Your progress.<br /><em>Made visible.</em></h2><p>Daily wins feel small. Nexal connects them into a clear story so you can see what is working—and keep going.</p><ul><li><i>✓</i> Weekly progress and habit insights</li><li><i>✓</i> Clear calorie and macro trends</li><li><i>✓</i> Streaks that reward consistency</li></ul></div>
      <div className="analytics-card"><div className="analytics-top"><div><small>MONTHLY OVERVIEW</small><h3>Your momentum</h3></div><span>Last 30 days⌄</span></div><div className="big-stat"><strong>86%</strong><span><b>↑ 14%</b> vs last month</span></div><div className="line-chart" aria-label="Rising monthly progress graph"><div className="grid-lines"><i/><i/><i/><i/></div><div className="trend-fill"/><div className="trend-line"><i/><i/><i/><i/><i/><i/></div></div><div className="chart-labels"><span>Week 1</span><span>Week 2</span><span>Week 3</span><span>Week 4</span></div><div className="metric-row"><div><i className="dot lime"/><span><small>WORKOUTS</small><b>18 completed</b></span></div><div><i className="dot green"/><span><small>NUTRITION</small><b>24 days on target</b></span></div></div></div>
    </div></section>

    <section className="premium-section shell" id="premium"><div className="premium-card"><div className="premium-glow"/><div className="premium-copy"><span className="section-kicker light">NEXAL PREMIUM</span><h2>A plan built<br />around <em>you.</em></h2><p>Unlock adaptive AI workout and meal planning, intelligent food tools and a routine that evolves with your progress.</p><div className="premium-pills"><span>✦ AI workout plans</span><span>✦ AI meal plans</span><span>✦ Barcode scanning</span></div><PlayButton /></div><div className="premium-phone"><div className="premium-screen"><span>YOUR WEEK</span><h3>Stronger, one day at a time.</h3>{["Upper body strength","Balanced meal plan","Lower body power"].map((item,index)=><div key={item} className="premium-row"><i>{index+1}</i><span><small>{index===1?"NUTRITION":"WORKOUT"}</small><b>{item}</b></span><em>{index===1?"View":"Start"}</em></div>)}</div></div></div></section>

    <section className="faq-section shell"><div><span className="section-kicker">THE DETAILS</span><h2>Good questions.<br /><em>Clear answers.</em></h2></div><div className="faq-list"><details open><summary>Can I use Nexal for free?<span>+</span></summary><p>Yes. Core meal, calorie, workout and progress tracking are free. Premium unlocks AI planning and advanced tools.</p></details><details><summary>Does Nexal create personal plans?<span>+</span></summary><p>Nexal Premium uses your goals, preferences and activity to build personalized workout and meal plans.</p></details><details><summary>Is Nexal available on iPhone?<span>+</span></summary><p>Nexal is currently available for Android through Google Play.</p></details></div></section>

    <section className="final-cta"><div className="shell"><BrandMark/><h2>Your next chapter<br />starts <em>today.</em></h2><p>Track for free. Upgrade when you are ready for AI-powered guidance.</p><PlayButton/></div></section>
    <footer><div className="shell"><a href="#top" className="brand"><BrandMark/><strong>Nexal</strong></a><p>Fitness, nutrition and progress—connected.</p><div><a href="https://play.google.com/store/apps/details?id=com.nexal.app">Google Play</a><a href="mailto:support@nexal.app">Support</a><a href="/privacy">Privacy</a></div><span>© 2026 Nexal</span></div></footer>
  </main>;
}
