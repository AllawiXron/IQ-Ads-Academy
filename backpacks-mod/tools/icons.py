"""16x16 pixel art backpack icons, drawn from one template with a palette per tier."""
from PIL import Image

# O outline, d/m/l/h body shadow/mid/light/highlight, F/f/g flap, P/p/q pocket, S/s strap, K/k metal, E/e gem
TEMPLATE = [
    "................",
    "......OOOO......",
    ".....Os..sO.....",
    "...OOOOOOOOOO...",
    "..OgggggggggfO..",
    "..OgffffffffFO..",
    "..OgfffkKfffFO..",
    "..OFFFFKKFFFFO..",
    "..Ohmmm" "sS" "mmmdO..",
    ".OmhldddsSdlddO.",
    ".OmhldqqqpdlddO.",
    ".OmhldqpppdlddO.",
    ".OmhldpPPPdlddO.",
    ".OmdlddddddmddO.",
    "..OdmmmmmmmmdO..",
    "...OOOOOOOOOO...",
]

# leather has to be dyeable: everything except the straps and metal is drawn in gray on layer 0 and tinted in game
TINTED = set("OdmlhFfgPpq")


def mix(a, b, t):
    return tuple(int(x + (y - x) * t) for x, y in zip(a, b))


def scale(c, k):
    return tuple(max(0, min(255, int(v * k))) for v in c)


BROWN_STRAP = ((70, 44, 24), (112, 72, 40))
TIERS = {
    #            outline        dark            mid             light           highlight        strap          metal (dark, light)              gem (dark, light)
    'leather': ((44, 44, 44), (118, 118, 118), (162, 162, 162), (202, 202, 202), (232, 232, 232), BROWN_STRAP, ((112, 112, 120), (206, 206, 214)), None),
    'iron': ((46, 46, 52), (124, 124, 132), (172, 172, 180), (210, 210, 218), (240, 240, 246), BROWN_STRAP, ((88, 88, 96), (150, 150, 160)), None),
    'golden': ((96, 58, 8), (196, 136, 18), (236, 186, 46), (255, 222, 96), (255, 246, 182), BROWN_STRAP, ((168, 112, 16), (255, 250, 210)), None),
    'diamond': ((14, 66, 70), (34, 156, 158), (76, 212, 202), (148, 240, 230), (214, 255, 250), ((44, 52, 74), (72, 84, 112)), None, ((40, 150, 230), (232, 252, 255))),
    'emerald': ((8, 56, 28), (18, 136, 58), (38, 188, 88), (106, 232, 146), (186, 255, 206), BROWN_STRAP, None, ((4, 110, 46), (170, 255, 196))),
    'netherite': ((22, 20, 24), (52, 48, 54), (76, 70, 78), (102, 95, 102), (132, 124, 130), ((36, 30, 34), (58, 50, 54)), ((122, 92, 64), (196, 164, 124)), None),
    'ender': ((6, 24, 28), (16, 56, 60), (26, 84, 86), (42, 116, 112), (68, 150, 140), ((40, 20, 58), (72, 38, 100)), ((112, 56, 168), (196, 126, 236)), ((18, 104, 76), (96, 232, 164))),
}


def palette(tier):
    o, d, m, l, h, strap, metal, gem = TIERS[tier]
    p = {
        'O': o, 'd': d, 'm': m, 'l': l, 'h': h,
        'F': scale(d, 0.8), 'f': mix(d, m, 0.35), 'g': mix(m, l, 0.5),
        'P': mix(d, m, 0.5), 'p': m, 'q': l,
        'S': strap[0], 's': strap[1],
    }
    if metal:
        p['K'], p['k'] = metal
    if gem:  # a gem takes the buckle's place
        p['K'], p['k'] = gem
    return p


def draw(tier):
    pal = palette(tier)
    rows = [list(r) for r in TEMPLATE]
    if tier == 'iron':  # rivets on the flap
        rows[5][4] = 'k'
        rows[5][11] = 'k'
    if tier == 'golden':  # shiny trim along the flap edge
        for x in range(4, 12):
            if rows[7][x] == 'F':
                rows[7][x] = 'd'
    if tier == 'ender':  # the eye on the pocket
        rows[10][7], rows[10][8] = 'k', 'k'
        rows[11][7], rows[11][8] = 'K', 'K'
        pal['k'], pal['K'] = TIERS['ender'][7][1], TIERS['ender'][7][0]
        rows[6][7], rows[6][8], rows[7][7], rows[7][8] = 'f', 'f', 'F', 'F'
    img = Image.new('RGBA', (16, 16), (0, 0, 0, 0))
    px = img.load()
    for y, row in enumerate(rows):
        for x, ch in enumerate(row):
            if ch != '.':
                px[x, y] = pal[ch] + (255,)
    if tier == 'netherite':  # rough netherite speckle
        import random
        rnd = random.Random(7)
        for _ in range(9):
            x, y = rnd.randint(4, 11), rnd.randint(8, 14)
            if rows[y][x] in 'lm':
                px[x, y] = pal['d'] + (255,)
    return img, rows


def leather_layers():
    """Layer 0 = gray body for the dye tint, layer 1 = straps and buckle that keep their colour."""
    img, rows = draw('leather')
    base = Image.new('RGBA', (16, 16), (0, 0, 0, 0))
    over = Image.new('RGBA', (16, 16), (0, 0, 0, 0))
    for y, row in enumerate(rows):
        for x, ch in enumerate(row):
            if ch == '.':
                continue
            (base if ch in TINTED else over).putpixel((x, y), img.getpixel((x, y)))
    return base, over
